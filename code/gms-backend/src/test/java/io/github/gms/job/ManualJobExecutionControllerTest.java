package io.github.gms.job;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.job.model.UrlConstants;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ManualJobExecutionControllerTest extends AbstractUnitTest {

    @Mock
    private ApplicationContext applicationContext;
    @InjectMocks
    private ManualJobExecutionController manualJobExecutionController;

    @Test
    void runJobByName_whenJobNotFound_thenReturnNotFound() {
        // given
        String jobName = "jobName";

        // when
        ResponseEntity<Void> response = manualJobExecutionController.runJobByName(jobName);

        // then
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    void runJobByName_whenJobFound_thenReturnOk() {
        // given
        EventMaintenanceJob mockJob = mock(EventMaintenanceJob.class);
        when(applicationContext.getBean(EventMaintenanceJob.class)).thenReturn(mockJob);

        // when
        ResponseEntity<Void> response = manualJobExecutionController.runJobByName(UrlConstants.EVENT_MAINTENANCE);

        // then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void runJobByName_whenJobBeanNotFound_thenReturnNotFound() {
        // given
        when(applicationContext.getBean(EventMaintenanceJob.class)).thenThrow(NoSuchBeanDefinitionException.class);

        // when
        ResponseEntity<Void> response = manualJobExecutionController.runJobByName(UrlConstants.EVENT_MAINTENANCE);

        // then
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
}
