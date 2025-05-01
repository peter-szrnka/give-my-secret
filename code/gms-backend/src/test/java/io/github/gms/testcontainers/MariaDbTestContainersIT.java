package io.github.gms.testcontainers;

import io.github.gms.abstraction.AbstractTestContainersIntegrationTest;
import io.github.gms.functions.announcement.AnnouncementDto;
import io.github.gms.functions.announcement.AnnouncementService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("mariadb")
@Disabled("Only for local testing")
public class MariaDbTestContainersIT extends AbstractTestContainersIntegrationTest {
    static MariaDBContainer<?> container = new MariaDBContainer<>("mariadb:10.3.34");

    @Autowired
    private AnnouncementService announcementService;

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

    @Test
    void announcementsGetById_thenReturn() {
        AnnouncementDto response = announcementService.getById(1L);

        assertNotNull(response);
    }
}