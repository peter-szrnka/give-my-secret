package io.github.gms.testcontainers;

import io.github.gms.abstraction.AbstractTestContainersIntegrationTest;
import io.github.gms.functions.announcement.AnnouncementDto;
import io.github.gms.functions.announcement.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractAnnouncementTestContainersIT  extends AbstractTestContainersIntegrationTest  {

    @Autowired
    private AnnouncementService announcementService;

    @Test
    void announcementsGetById_thenReturn() {
        AnnouncementDto response = announcementService.getById(1L);

        assertNotNull(response);
    }
}
