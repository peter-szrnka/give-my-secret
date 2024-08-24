package io.github.gms;

import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.abstraction.GmsController;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

/**
 * Checks that all endpoints are covered by integration tests.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class IntegrationAnnotationCheckerTest {
    private static final Set<String> IGNORED_METHODS = Set.of(
            "$jacocoInit", "equals", "hashCode", "toString", "notify", "notifyAll", "wait", "getClass", "finalize", "wait0", "clone"
    );
    private static final Map<String, ClassData> controllers;
    private static final Map<String, TestClassData> tests;

    static {
        try {
            controllers = getAllControllerClasses();
            tests = getAllTestClasses();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldHaveIntegrationAnnotation() {
        controllers.forEach((k, v) -> {
            assertNotNull("Integration test is missing for " + k, tests.containsKey(k));

            TestClassData testClassData = tests.get(k);

            if (testClassData.isSkip()) {
                return;
            }

            assertEquals(k + " has some untested methods!", v.getMethods(), tests.get(k).getMethods());
        });
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

    private static Map<String, ClassData> getAllControllerClasses() throws ClassNotFoundException {
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
                    .filter(name -> !IGNORED_METHODS.contains(name))
                    .collect(Collectors.toSet()));

            classData.setMethods(controllerMethods);
            resultMap.put(controller.getSimpleName(), classData);
        }

        return resultMap;
    }

    private static Map<String, TestClassData> getAllTestClasses() throws ClassNotFoundException {
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

    private static Set<Class<?>> getAllSubClasses(Class<?> clazz) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(clazz));

        Set<Class<?>> resultList = new HashSet<>();
        Set<BeanDefinition> components = provider.findCandidateComponents("io.github.gms");

        for (BeanDefinition component : components) {
            Class<?> resultClass = Class.forName(component.getBeanClassName());
            resultList.add(resultClass);
        }

        return resultList;
    }
}
