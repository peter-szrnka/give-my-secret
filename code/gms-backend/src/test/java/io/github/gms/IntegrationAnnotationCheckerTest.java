package io.github.gms;

import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.abstraction.GmsController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks that all endpoints are covered by integration tests.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled("TODO Implement this test")
class IntegrationAnnotationCheckerTest {

    @Test
    void shouldHaveIntegrationAnnotation() throws ClassNotFoundException {
        // arrange
        Set<Class<?>> controllers = getAllSubClasses(GmsController.class);
        Set<Class<?>> tests = getAllSubClasses(GmsControllerIntegrationTest.class);

        printSet(controllers);
        System.out.println("=====================================");
        printSet(tests);

        Set<String> missingTests = new HashSet<>();

        // assert
        controllers.forEach(controller -> {
            AtomicBoolean found = new AtomicBoolean(false);
            tests.forEach(test -> {
                TestedClass testedClassAnnotation = test.getAnnotation(TestedClass.class);
                // + assertThat(testedClassAnnotation).isNotNull();
                if (testedClassAnnotation != null && testedClassAnnotation.value().equals(controller)) {
                    found.set(true);
                }
            });

            if (!found.get()) {
                missingTests.add(controller.getSimpleName());
            }
        });

        // Checks that all controllers have a corresponding integration test
        // + assertThat(controllers.size()).isEqualTo(tests.size());
        // + assertThat(missingTests).isEmpty();
    }

    private static void printSet(Set<Class<?>> set) {
        Set<String> orderedNames = set.stream()
                .map(Class::getSimpleName).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        for (String clazz : orderedNames) {
            System.out.println(clazz);
        }
    }

    private static Set<Class<?>> getAllSubClasses(Class<?> clazz) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(clazz));

        Set<Class<?>> resultList = new HashSet<>();
        Set<BeanDefinition> components = provider.findCandidateComponents("io.github.gms");

        for (BeanDefinition component : components)
        {
            Class<?> resultClass = Class.forName(component.getBeanClassName());
            resultList.add(resultClass);

            Stream.of(resultClass.getDeclaredMethods()).forEach(method -> {
                System.out.println(resultClass.getSimpleName() + " - " + method.getName());
            });
        }

        return resultList;
    }
}
