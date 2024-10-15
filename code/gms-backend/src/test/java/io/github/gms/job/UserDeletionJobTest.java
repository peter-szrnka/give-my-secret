package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.gdpr.UserAssetDeletionService;
import io.github.gms.functions.gdpr.UserDeletionService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.LogAssertionUtils.assertLogMissing;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserDeletionJobTest extends AbstractLoggingUnitTest {

    private SystemService systemService;
    private SystemPropertyService systemPropertyService;
    private UserDeletionService userDeletionService;
    private UserAssetDeletionService userAssetDeletionService;
    private UserDeletionJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        userDeletionService = mock(UserDeletionService.class);
        userAssetDeletionService = mock(UserAssetDeletionService.class);
        job = new UserDeletionJob(systemService, systemPropertyService, userDeletionService, userAssetDeletionService);
        addAppender(UserDeletionJob.class);
    }

    @Test
    void execute_whenJobIsDisabled_thenSkipExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(false);

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
    }

    @Test
    void execute_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
        // arrange
        when(systemService.getContainerId()).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.USER_DELETION_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).get(SystemProperty.USER_DELETION_RUNNER_CONTAINER_ID);
        verify(userDeletionService, never()).getRequestedUserDeletionIds();
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
    }

    @Test
    void shouldSkipProcessing() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(true);
        when(userDeletionService.getRequestedUserDeletionIds()).thenReturn(Collections.emptySet());

        // act
        job.execute();

        // assert
        verify(userDeletionService).getRequestedUserDeletionIds();
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
        assertLogMissing(logAppender, "Deleting requested user assets(API keys, secrets, keystore resources,etc.");
    }

    @Test
    void shouldProcess() {
        // arrange
        Set<Long> userIds = Set.of(1L, 2L);
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(true);
        when(userDeletionService.getRequestedUserDeletionIds()).thenReturn(userIds);

        // act
        job.execute();

        // assert
        verify(userDeletionService).getRequestedUserDeletionIds();
        verify(userAssetDeletionService).executeRequestedUserAssetDeletion(userIds);
        verify(userDeletionService).executeRequestedUserDeletion(userIds);
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
        assertLogContains(logAppender, "2 user(s) requested to delete");
        assertLogContains(logAppender, "Deleting requested user assets(API keys, secrets, keystore resources,etc.");
        assertLogContains(logAppender, "Deleting requested users");
    }
}
