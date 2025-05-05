package io.github.gms.testcontainers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ActiveProfiles("mariadb")
@Tag(TAG_INTEGRATION_TEST)
@EnabledIfEnvironmentVariable(named = "TEST_CONTAINERS_TEST_ENABLED", matches = "true")
public class MariaDbTestContainersIT extends AbstractAnnouncementTestContainersIT {
    static MariaDBContainer<?> container = new MariaDBContainer<>("mariadb:10.3.34");

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