package io.github.gms.functions.user;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.LogAssertionUtils.assertLogMissing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserLoginAttemptManagerServiceImplTest extends AbstractLoggingUnitTest {

    private UserRepository repository;
    private SystemPropertyService systemPropertyService;
    private UserLoginAttemptManagerServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        repository = mock(UserRepository.class);
        systemPropertyService = mock(SystemPropertyService.class);
        service = new UserLoginAttemptManagerServiceImpl(repository, systemPropertyService);
        addAppender(UserLoginAttemptManagerServiceImpl.class);
    }

    @Test
    void updateLoginAttempt_whenUserNotFound_thenSkipUpdate() {
        // arrange
        when(repository.findByUsername("user1")).thenReturn(Optional.empty());

        // act
        service.updateLoginAttempt("user1");

        // assert
        verify(repository).findByUsername("user1");
        assertLogMissing(logAppender, "User already blocked");
        verify(repository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateLoginAttempt_whenUserIsAlreadyBlocked_thenSkipUpdate() {
        // arrange
        UserEntity mockEntity = TestUtils.createUser();
        mockEntity.setStatus(EntityStatus.BLOCKED);
        when(repository.findByUsername("user1")).thenReturn(Optional.of(mockEntity));

        // act
        service.updateLoginAttempt("user1");

        // assert
        assertLogContains(logAppender, "User already blocked");
        verify(systemPropertyService, never()).getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);
    }

    @Test
    void updateLoginAttempt_whenUserFailedAttemptsIsZero_thenIncrementFailedAttempts() {
        // arrange
        UserEntity mockEntity = TestUtils.createUser();
        when(repository.findByUsername("user1")).thenReturn(Optional.of(mockEntity));
        when(systemPropertyService.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT)).thenReturn(3);

        // act
        service.updateLoginAttempt("user1");

        // assert
        verify(systemPropertyService).getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(userEntityArgumentCaptor.capture());
        assertEquals(1, userEntityArgumentCaptor.getValue().getFailedAttempts());
    }

    @Test
    void updateLoginAttempt_whenUserReachedFailedAttemptsLimit_thenBlockUser() {
        // arrange
        UserEntity mockEntity = TestUtils.createUser();
        mockEntity.setFailedAttempts(2);
        when(repository.findByUsername("user1")).thenReturn(Optional.of(mockEntity));
        when(systemPropertyService.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT)).thenReturn(3);

        // act
        service.updateLoginAttempt("user1");

        // assert
        verify(systemPropertyService).getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(userEntityArgumentCaptor.capture());
        assertEquals(EntityStatus.BLOCKED, userEntityArgumentCaptor.getValue().getStatus());
    }

    @Test
    void resetLoginAttempt_whenUserFound_thenReset() {
        // arrange
        UserEntity mockEntity = TestUtils.createUser();
        mockEntity.setFailedAttempts(3);
        mockEntity.setStatus(EntityStatus.BLOCKED);
        when(repository.findByUsername("user1")).thenReturn(Optional.of(mockEntity));

        // act
        service.resetLoginAttempt("user1");

        // assert
        ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(userEntityArgumentCaptor.capture());
        assertEquals(0, userEntityArgumentCaptor.getValue().getFailedAttempts());
    }

    @Test
    void resetLoginAttempt_whenUserNotFound_thenSkipReset() {
        // arrange
        when(repository.findByUsername("user1")).thenReturn(Optional.empty());

        // act
        service.resetLoginAttempt("user1");

        // assert
        verify(repository, never()).save(any(UserEntity.class));
        verify(repository).findByUsername("user1");
    }

    @ParameterizedTest
    @MethodSource("isBlockedTestData")
    void isBlocked_whenInputContainsDifferentStatus_thenReturnExpectedResult(EntityStatus inputStatus, boolean expectedResult) {
        // arrange
        UserEntity mockEntity = TestUtils.createUser();
        mockEntity.setFailedAttempts(3);
        mockEntity.setStatus(inputStatus);
        when(repository.findByUsername("user1")).thenReturn(Optional.of(mockEntity));

        // act
        boolean response = service.isBlocked("user1");

        // assert
        assertEquals(expectedResult, response);
    }

    @Test
    void isBlocked_whenUserNotFound_thenReturnFalse() {
        // arrange
        when(repository.findByUsername("user1")).thenReturn(Optional.empty());

        // act
        boolean response = service.isBlocked("user1");

        // assert
        assertFalse(response);
    }

    private static Object[][] isBlockedTestData() {
        return new Object[][]{
                {EntityStatus.BLOCKED, true},
                {EntityStatus.ACTIVE, false}
        };
    }
}
