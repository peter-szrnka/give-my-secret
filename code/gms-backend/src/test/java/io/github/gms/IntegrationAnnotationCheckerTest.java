package io.github.gms;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.abstraction.GmsControllerSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.types.SkipSecurityTestCheck;
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
import static org.springframework.test.util.AssertionErrors.*;

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
    private static Map<String, ClassData> controllers;
    private static final Map<String, TestClassData> integrationTests;
    private static final Map<String, TestClassData> securityTests;

    static {
        try {
            integrationTests = getAllIntegrationTestClasses();
            securityTests = getAllSecurityTestClasses();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldControllerHaveProperIntegrationTests() throws Exception {
        controllers = getAllControllerClasses(false);
        AtomicInteger skipCounter = new AtomicInteger(0);
        controllers.forEach((k, v) -> assertController(skipCounter, k, v));

        // Threshold for the number of classes & methods that is skipped temporarily
        assertThat(skipCounter.get()).isLessThan(FAILURE_THRESHOLD);
    }

    @Test
    void shouldControllerHaveProperSecurityTests() throws Exception {
        controllers = getAllControllerClasses(true);
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

            final Set<String> postFilteredMethods = Sets.difference(postFilterMethods(v.getMethods()), testClassData.getMethods());
            if (!postFilteredMethods.isEmpty()) {
                missingSecurityTestMethods.put(k, postFilteredMethods);
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

    private static Map<String, ClassData> getAllControllerClasses(boolean securityTestCheck) throws Exception {
        Map<String, ClassData> resultMap = new HashMap<>();
        Set<Class<?>> controllers = getAllSubClasses(GmsController.class);

        for (Class<?> controller : controllers) {
            SkipSecurityTestCheck skipTestAnnotationCheck = controller.getAnnotation(SkipSecurityTestCheck.class);

            if (skipTestAnnotationCheck != null) {
                continue;
            }

            ClassData classData = new ClassData();
            Set<String> controllerMethods = Stream.of(controller.getDeclaredMethods())
                    .filter(method -> !securityTestCheck || method.getAnnotation(SkipSecurityTestCheck.class) == null)
                    .map(Method::getName)
                    .filter(name -> !name.startsWith("lambda$")).collect(Collectors.toSet());

            controllerMethods.addAll(Stream.of(controller.getSuperclass().getDeclaredMethods())
                    .filter(method -> Modifier.isPublic(method.getModifiers()))
                    .filter(method -> !IGNORED_METHODS.contains(method.getName()) && !method.getName().contains("$"))
                    .filter(method -> !securityTestCheck || method.getAnnotation(SkipSecurityTestCheck.class) == null)
                    .map(Method::getName)
                    .collect(Collectors.toSet()));

            classData.setMethods(controllerMethods);
            resultMap.put(controller.getSimpleName(), classData);
        }

        return resultMap;
    }

    private static Map<String, TestClassData> getAllIntegrationTestClasses() throws Exception {
        return getAllSpecificTestClasses("integration", GmsControllerIntegrationTest.class);
    }

    private static Map<String, TestClassData> getAllSecurityTestClasses() throws Exception {
        return getAllSpecificTestClasses("security", GmsControllerSecurityTest.class);
    }

    private static Map<String, TestClassData> getAllSpecificTestClasses(String scope, Class<?> clazz) throws Exception {
        Map<String, TestClassData> resultMap = new HashMap<>();
        Set<Class<?>> testClasses = getAllSubClasses(clazz);

        for (Class<?> test : testClasses) {
            TestedClass testedClassAnnotation = test.getAnnotation(TestedClass.class);
            assertNotNull("Annotation @TestedClass is missing from " + test.getSimpleName(), testedClassAnnotation);
            String className = "-";
            Class<?> originalClass = testedClassAnnotation.value();
            boolean skip = testedClassAnnotation.skip();
            className = originalClass.getSimpleName();

            TestClassData classData = new TestClassData();
            Set<String> testMethods = Stream.of(test.getDeclaredMethods())
                    .filter(method -> method.getAnnotation(TestedMethod.class) != null)
                    .map(methods -> {
                        TestedMethod testedMethodAnnotation = methods.getAnnotation(TestedMethod.class);
                        return testedMethodAnnotation.value();
                    })
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