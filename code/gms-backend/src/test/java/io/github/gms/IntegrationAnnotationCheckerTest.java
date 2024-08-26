package io.github.gms;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.abstraction.GmsControllerSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.types.SkipTestAnnotationCheck;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Checks that all endpoints are covered by integration tests.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class IntegrationAnnotationCheckerTest {
    // Test settings
    private static final int FAILURE_THRESHOLD = 3;
    private static final boolean SKIP_ENABLED = false;

    private static final Set<String> IGNORED_METHODS = Set.of(
            "$jacocoInit", "equals", "hashCode", "toString", "notify", "notifyAll", "wait", "getClass", "finalize", "wait0", "clone"
    );
    private static final Map<String, ClassData> controllers;
    private static final Map<String, TestClassData> integrationTests;
    private static final Map<String, TestClassData> securityTests;

    static {
        try {
            controllers = getAllControllerClasses();
            integrationTests = getAllIntegrationTestClasses();
            securityTests = getAllSecurityTestClasses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldControllerHaveProperIntegrationTests() {
        AtomicInteger skipCounter = new AtomicInteger(0);
        controllers.forEach((k, v) -> assertController(skipCounter, k, v));

        // Threshold for the number of classes & methods that is skipped temporarily
        assertThat(skipCounter.get()).isLessThan(FAILURE_THRESHOLD);
    }

    @Test
    void shouldControllerHaveProperSecurityTests() {
        AtomicInteger skipCounter = new AtomicInteger(0);
        Set<String> missingSecurityTests = new HashSet<>();
        Map<String, Set<String>> missingSecurityTestMethods = new HashMap<>();
        controllers.forEach((k, v) -> {
            if (!securityTests.containsKey(k)) {
                missingSecurityTests.add(k);
                return;
            }

            TestClassData testClassData = securityTests.get(k);
            if (SKIP_ENABLED && testClassData.isSkip()) {
                skipCounter.incrementAndGet();
                return;
            }

            final Set<String> postFilteredMethods = postFilterMethods(v.getMethods());
            if (!postFilteredMethods.equals(testClassData.getMethods())) {
                missingSecurityTestMethods.put(k, Sets.difference(postFilteredMethods, testClassData.getMethods()));
            }
        });

        // Threshold for the number of classes & methods that is skipped temporarily
        assertThat(skipCounter.get()).isLessThan(FAILURE_THRESHOLD);
        assertTrue("Missing security tests: " + String.join(", \r\n", missingSecurityTests), missingSecurityTests.isEmpty());
        assertTrue("Missing security test methods: " + printUncoveredTestMethods(missingSecurityTestMethods), missingSecurityTestMethods.isEmpty());
    }

    @Getter
    @Setter
    static class ClassData {
        private Set<String> methods = new HashSet<>();
    }

    @Getter
    @Setter
    static class TestClassData {
        private String testClassName;
        private Set<String> methods = new HashSet<>();
        private boolean skip;
    }

    private static Map<String, ClassData> getAllControllerClasses() throws Exception {
        Map<String, ClassData> resultMap = new HashMap<>();
        Set<Class<?>> controllers = getAllSubClasses(GmsController.class);

        for (Class<?> controller : controllers) {
            SkipTestAnnotationCheck skipTestAnnotationCheck = controller.getAnnotation(SkipTestAnnotationCheck.class);

            if (skipTestAnnotationCheck != null) {
                continue;
            }

            ClassData classData = new ClassData();
            Set<String> controllerMethods = Stream.of(controller.getDeclaredMethods())
                    .map(Method::getName)
                    .filter(name -> !name.startsWith("lambda$")).collect(Collectors.toSet());

            controllerMethods.addAll(Stream.of(controller.getSuperclass().getDeclaredMethods())
                    .filter(method -> Modifier.isPublic(method.getModifiers()))
                    .map(Method::getName)
                    .filter(name -> !IGNORED_METHODS.contains(name) && !name.contains("$"))
                    .collect(Collectors.toSet()));

            classData.setMethods(controllerMethods);
            resultMap.put(controller.getSimpleName(), classData);
        }

        return resultMap;
    }

    private static Map<String, TestClassData> getAllIntegrationTestClasses() throws Exception {
        return getAllSpecificTestClasses(GmsControllerIntegrationTest.class);
    }

    private static Map<String, TestClassData> getAllSecurityTestClasses() throws Exception {
        return getAllSpecificTestClasses(GmsControllerSecurityTest.class);
    }

    private static Map<String, TestClassData> getAllSpecificTestClasses(Class<?> clazz) throws Exception {
        Map<String, TestClassData> resultMap = new HashMap<>();
        Set<Class<?>> testClasses = getAllSubClasses(clazz);

        for (Class<?> test : testClasses) {
            TestedClass testedClassAnnotation = test.getAnnotation(TestedClass.class);
            String className = "-";
            Class<?> originalClass = null;
            boolean skip = false;

            if (testedClassAnnotation != null) {
                originalClass = testedClassAnnotation.value();
                className = originalClass.getSimpleName();
                skip = testedClassAnnotation.skip();
            }

            TestClassData classData = new TestClassData();
            Set<String> testMethods = Stream.of(test.getDeclaredMethods())
                    .filter(method -> method.getAnnotation(TestedMethod.class) != null)
                    .map(methods -> {
                        TestedMethod testedMethodAnnotation = methods.getAnnotation(TestedMethod.class);
                        return testedMethodAnnotation.value();
                    })
                    // TODO Filter methods that is marked with @SkipTestAnnotationCheck
                    .collect(Collectors.toSet());
            classData.setTestClassName(test.getSimpleName());
            classData.setMethods(testMethods);
            classData.setSkip(skip);
            resultMap.put(className, classData);
        }

        return resultMap;
    }

    private static Set<Class<?>> getAllSubClasses(Class<?> inputClazz) throws Exception {
        Reflections reflections = new Reflections("io.github.gms");
        return reflections.getSubTypesOf(inputClazz).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).collect(Collectors.toSet());
    }

    private static void assertController(
            AtomicInteger skipCounter,
            String key,
            ClassData classData) {
        assertTrue("Integration test is missing for " + key, integrationTests.containsKey(key));

        TestClassData testClassData = integrationTests.get(key);
        if (SKIP_ENABLED && testClassData.isSkip()) {
            skipCounter.incrementAndGet();
            return;
        }

        assertEquals(key + " has some untested methods!", postFilterMethods(classData.getMethods()), integrationTests.get(key).getMethods());
    }

    private static Set<String> postFilterMethods(Set<String> methodNames) {
        return methodNames.stream().filter(name -> !name.contains("$")).collect(Collectors.toSet());
    }

    private static String printUncoveredTestMethods(Map<String, Set<String>> missingSecurityTestMethods) {
        StringBuilder sb = new StringBuilder("\r\n");
        missingSecurityTestMethods.forEach((k, v) -> {
            sb.append(k).append(": ").append(v).append("\r\n");
        });

        return sb.toString();
    }
}