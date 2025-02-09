package io.github.gms.functions.maintenance;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.maintenance.model.BatchUserOperationDto;
import io.github.gms.functions.maintenance.user.UserAnonymizationService;
import io.github.gms.functions.maintenance.user.UserDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test of {@link MaintenanceController}
 *
 * @author Peter Szrnka
 */
class MaintenanceControllerTest extends AbstractUnitTest {

    private UserAnonymizationService userAnonymizationService;
    private UserDeletionService userDeletionService;
    private MaintenanceController controller;

    @BeforeEach
    void setupTest() {
        userAnonymizationService = mock(UserAnonymizationService.class);
        userDeletionService = mock(UserDeletionService.class);
        controller = new MaintenanceController(userAnonymizationService, userDeletionService);
    }

    @Test
    void requestUserAnonymization_whenCorrectInputProvided_thenComplete() {
        // arrange
        BatchUserOperationDto input = BatchUserOperationDto.builder().build();

        // act
        ResponseEntity<Void> response = controller.requestUserAnonymization(input);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userAnonymizationService).requestProcess(input);
    }

    @Test
    void requestUserDeletion_whenCorrectInputProvided_thenComplete() {
        // arrange
        BatchUserOperationDto input = BatchUserOperationDto.builder().build();

        // act
        ResponseEntity<Void> response = controller.requestUserDeletion(input);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userDeletionService).requestProcess(input);
    }
}
