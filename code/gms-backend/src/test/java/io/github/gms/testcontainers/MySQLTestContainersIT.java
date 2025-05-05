package io.github.gms.testcontainers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ActiveProfiles("mysql")
@Tag(TAG_INTEGRATION_TEST)
@EnabledIfEnvironmentVariable(named = "TEST_CONTAINERS_TEST_ENABLED", matches = "true")
public class MySQLTestContainersIT extends AbstractAnnouncementTestContainersIT {
    static MySQLContainer<?> container = new MySQLContainer<>("mysql:8.0.34");

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        initProperties(registry, container);
    }
}