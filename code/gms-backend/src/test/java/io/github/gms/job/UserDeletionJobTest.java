package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.UserAssetDeletionService;
import io.github.gms.functions.maintenance.UserDeletionService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Set;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.LogAssertionUtils.assertLogMissing;
import static io.github.gms.util.TestUtils.createJobEntity;
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
    private Clock clock;
    private JobRepository jobRepository;
    private UserDeletionJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        userDeletionService = mock(UserDeletionService.class);
        userAssetDeletionService = mock(UserAssetDeletionService.class);
        clock = mock(Clock.class);
        jobRepository = mock(JobRepository.class);
        job = new UserDeletionJob(userDeletionService, userAssetDeletionService);
        ReflectionTestUtils.setField(job, "systemService", systemService);
        ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
        ReflectionTestUtils.setField(job, "clock", clock);
        ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
        addAppender(UserDeletionJob.class);
    }

    @Test
    void run_whenJobIsDisabled_thenSkipExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(false);

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
    }

    @Test
    void run_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
        // arrange
        when(systemService.getContainerId()).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.USER_DELETION_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).get(SystemProperty.USER_DELETION_RUNNER_CONTAINER_ID);
        verify(userDeletionService, never()).getRequestedUserIds();
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
    }

    @Test
    void shouldSkipProcessing() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(true);
        when(userDeletionService.getRequestedUserIds()).thenReturn(Collections.emptySet());
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
        when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // act
        job.run();

        // assert
        verify(userDeletionService).getRequestedUserIds();
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
        assertLogMissing(logAppender, "Deleting requested user assets(API keys, secrets, keystore resources,etc.");
        verify(jobRepository, times(2)).save(any(JobEntity.class));
        verify(jobRepository).findById(anyLong());
    }

    @Test
    void shouldProcess() {
        // arrange
        Set<Long> userIds = Set.of(1L, 2L);
        when(systemPropertyService.getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED)).thenReturn(true);
        when(userDeletionService.getRequestedUserIds()).thenReturn(userIds);
        when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
        when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // act
        job.run();

        // assert
        verify(userDeletionService).getRequestedUserIds();
        verify(userAssetDeletionService).executeRequestedUserAssetDeletion(userIds);
        verify(userDeletionService).process(userIds);
        verify(systemPropertyService).getBoolean(SystemProperty.USER_DELETION_JOB_ENABLED);
        assertLogContains(logAppender, "2 user(s) requested to delete");
        assertLogContains(logAppender, "Deleting requested user assets(API keys, secrets, keystore resources,etc.");
        assertLogContains(logAppender, "Deleting requested users");
        verify(jobRepository, times(2)).save(any(JobEntity.class));
        verify(jobRepository).findById(anyLong());
    }
}
