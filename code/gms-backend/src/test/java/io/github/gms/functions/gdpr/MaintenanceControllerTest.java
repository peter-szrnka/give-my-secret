package io.github.gms.functions.gdpr;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.gdpr.model.BatchUserOperationDto;
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

    private UserDeletionService userDeletionService;
    private MaintenanceController controller;

    @BeforeEach
    void setupTest() {
        userDeletionService = mock(UserDeletionService.class);
        controller = new MaintenanceController(userDeletionService);
    }

    @Test
    void shouldRequestUserDeletion() {
        // arrange
        BatchUserOperationDto input = BatchUserOperationDto.builder().build();

        // act
        ResponseEntity<Void> response = controller.requestUserDeletion(input);

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userDeletionService).requestUserDeletion(input);
    }
}
