package io.github.gms;

import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.abstraction.GmsController;
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
    private static final Map<String, TestClassData> tests;

    static {
        try {
            controllers = getAllControllerClasses();
            tests = getAllTestClasses();
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

    private static Map<String, TestClassData> getAllTestClasses() throws Exception {
        Map<String, TestClassData> resultMap = new HashMap<>();
        Set<Class<?>> testClasses = getAllSubClasses(GmsControllerIntegrationTest.class);

        for (Class<?> test : testClasses) {
            TestedClass testedClassAnnotation = test.getAnnotation(TestedClass.class);
            String className = "-";
            boolean skip = false;

            if (testedClassAnnotation != null) {
                className = testedClassAnnotation.value().getSimpleName();
                skip = testedClassAnnotation.skip();
            }

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

    private static void assertController(AtomicInteger skipCounter, String key, ClassData classData) {
        assertTrue("Integration test is missing for " + key, tests.containsKey(key));

        TestClassData testClassData = tests.get(key);
        if (SKIP_ENABLED && testClassData.isSkip()) {
            skipCounter.incrementAndGet();
            return;
        }

        assertEquals(key + " has some untested methods!", postFilterMethods(classData.getMethods()), tests.get(key).getMethods());
    }

    private static Set<String> postFilterMethods(Set<String> methodNames) {
        return methodNames.stream().filter(name -> !name.contains("$")).collect(Collectors.toSet());
    }
}