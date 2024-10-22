package io.github.gms.functions.maintenance;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.abstraction.UserMaintenanceService;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.maintenance.model.BatchUserOperationDto;
import io.github.gms.functions.maintenance.user.UserDeletionService;
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
class UserDeletionServiceTest extends AbstractLoggingUnitTest {

    private UserRepository userRepository;
    private UserDeletionService service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        userRepository = mock(UserRepository.class);
        service = new UserDeletionService(userRepository);
        addAppender(UserMaintenanceService.class);
    }

    @Test
    void shouldRequestUserDeletion() {
        // arrange
        Set<Long> userIds = Set.of(1L);

        // act
        service.requestProcess(BatchUserOperationDto.builder().requestId("requestID").userIds(userIds).build());

        // assert
        verify(userRepository).batchUpdateStatus(userIds, EntityStatus.DELETE_REQUESTED);
        assertLogContains(logAppender, "Batch user deletion requested. requestId=requestID");
    }

    @Test
    void shouldGetRequestedUserDeletionIds() {
        // arrange
        Set<Long> userIds = Set.of(1L, 2L);
        when(userRepository.findAllByStatus(EntityStatus.DELETE_REQUESTED)).thenReturn(userIds);

        // act
        Set<Long> response = service.getRequestedUserIds();

        // arrange
        assertNotNull(response);
        assertEquals(userIds, response);
        verify(userRepository).findAllByStatus(EntityStatus.DELETE_REQUESTED);
    }

    @Test
    void shouldExecuteRequestedUserDeletion() {
        // arrange
        Set<Long> userIds = Set.of(1L);

        // act
        service.process(userIds);

        // assert
        verify(userRepository).deleteAllByUserId(userIds);
    }
}
