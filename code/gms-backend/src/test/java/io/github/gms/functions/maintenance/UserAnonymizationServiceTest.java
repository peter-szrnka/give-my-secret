package io.github.gms.functions.maintenance;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.abstraction.UserMaintenanceService;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.maintenance.model.BatchUserOperationDto;
import io.github.gms.functions.maintenance.user.UserAnonymizationService;
import io.github.gms.functions.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserAnonymizationServiceTest extends AbstractLoggingUnitTest {

    private UserRepository userRepository;
    private UserAnonymizationService service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        userRepository = mock(UserRepository.class);
        service = new UserAnonymizationService(userRepository);
        addAppender(UserMaintenanceService.class);
    }

    @Test
    void requestProcess_whenCorrectInputProvided_thenShouldRequestUserDeletion() {
        // arrange
        Set<Long> userIds = Set.of(1L);

        // act
        service.requestProcess(BatchUserOperationDto.builder().requestId("requestID").userIds(userIds).build());

        // assert
        verify(userRepository).batchUpdateStatus(userIds, EntityStatus.ANONYMIZATION_REQUESTED);
        assertLogContains(logAppender, "Batch user anonymization requested. requestId=requestID");
    }

    @Test
    void getRequestedUserIds_whenCorrectInputProvided_thenShouldReturnRequestedUserIds() {
        // arrange
        Set<Long> userIds = Set.of(1L, 2L);
        when(userRepository.findAllByStatus(EntityStatus.ANONYMIZATION_REQUESTED)).thenReturn(userIds);

        // act
        Set<Long> response = service.getRequestedUserIds();

        // arrange
        assertNotNull(response);
        assertEquals(userIds, response);
        verify(userRepository).findAllByStatus(EntityStatus.ANONYMIZATION_REQUESTED);
    }

    @Test
    void process_whenCorrectInputProvided_thenProcessUserDeletion() {
        // arrange
        Set<Long> userIds = Set.of(1L);

        // act
        service.process(userIds);

        // assert
        verify(userRepository).batchUpdateUserPersonalInfo(userIds);
    }
}
