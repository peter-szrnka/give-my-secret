package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class JobMaintenanceJobTest extends AbstractLoggingUnitTest {

    private SystemService systemService;
    private JobMaintenanceJob job;
    private Clock clock;
    private SystemPropertyService systemPropertyService;
    private JobRepository jobRepository;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        // init
        clock = mock(Clock.class);
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        jobRepository = mock(JobRepository.class);
        job = new JobMaintenanceJob();
        ReflectionTestUtils.setField(job, "systemService", systemService);
        ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
        ReflectionTestUtils.setField(job, "clock", clock);
        ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
        addAppender(JobMaintenanceJob.class);
    }

    @Test
    void run_whenJobIsDisabled_thenSkipExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.JOB_MAINTENANCE_JOB_ENABLED)).thenReturn(false);

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.JOB_MAINTENANCE_JOB_ENABLED);
    }

    @Test
    void run_whenAppIsNotRunningInMainContainer_thenSkipExecution() {
        // arrange
        when(systemService.getContainerId()).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.JOB_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemService).getContainerId();
        verify(systemPropertyService).get(SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID);
    }

    @Test
    void run_whenNoEntryAvailable_thenSkipJobExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.JOB_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn(null);
        when(systemPropertyService.get(SystemProperty.OLD_JOB_ENTRY_LIMIT)).thenReturn("1;d");
        when(jobRepository.findAllOld(any(ZonedDateTime.class))).thenReturn(List.of());
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(jobRepository).findAllOld(any(ZonedDateTime.class));
        verify(systemPropertyService).get(SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID);
        verify(systemPropertyService).get(SystemProperty.OLD_JOB_ENTRY_LIMIT);
    }

    @Test
    void run_whenOneEntryAvailable_thenRunJobExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.JOB_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn(null);
        when(systemPropertyService.get(SystemProperty.OLD_JOB_ENTRY_LIMIT)).thenReturn("1;d");
        when(jobRepository.findAllOld(any(ZonedDateTime.class))).thenReturn(List.of(TestUtils.createJobEntity()));
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // act
        job.run();

        // assert
        assertLogContains(logAppender, "1 old job log(s) deleted");
        verify(jobRepository).findAllOld(any(ZonedDateTime.class));
        verify(systemPropertyService).get(SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID);
        verify(systemPropertyService).get(SystemProperty.OLD_JOB_ENTRY_LIMIT);
    }
}
