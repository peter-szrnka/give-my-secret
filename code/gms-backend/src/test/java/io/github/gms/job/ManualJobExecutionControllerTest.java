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
        // arrange
        String jobName = "jobName";

        // act
        ResponseEntity<Void> response = manualJobExecutionController.runJobByName(jobName);

        // assert
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    void runJobByName_whenJobFound_thenReturnOk() {
        // arrange
        when(applicationContext.getBean(EventMaintenanceJob.class)).thenReturn(mock(EventMaintenanceJob.class));

        // act
        ResponseEntity<Void> response = manualJobExecutionController.runJobByName(UrlConstants.EVENT_MAINTENANCE);

        // assert
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void runJobByName_whenJobBeanNotFound_thenReturnNotFound() {
        // arrange
        when(applicationContext.getBean(EventMaintenanceJob.class)).thenThrow(NoSuchBeanDefinitionException.class);

        // act
        ResponseEntity<Void> response = manualJobExecutionController.runJobByName(UrlConstants.EVENT_MAINTENANCE);

        // assert
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
}