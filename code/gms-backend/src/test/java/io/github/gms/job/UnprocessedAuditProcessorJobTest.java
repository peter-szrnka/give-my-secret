package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.common.model.UserEvent;
import io.github.gms.functions.event.EventService;
import io.github.gms.functions.event.UnprocessedEventStorage;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createJobEntity;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UnprocessedAuditProcessorJobTest extends AbstractLoggingUnitTest {

    private UnprocessedEventStorage unprocessedEventStorage;
    private EventService eventService;
    private SystemService systemService;
    private Clock clock;
    private SystemPropertyService systemPropertyService;
    private JobRepository jobRepository;
    private SystemAttributeRepository systemAttributeRepository;
    private UnprocessedAuditProcessorJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        clock = mock(Clock.class);
        jobRepository = mock(JobRepository.class);
        systemAttributeRepository = mock(SystemAttributeRepository.class);
        unprocessedEventStorage = mock(UnprocessedEventStorage.class);
        eventService = mock(EventService.class);
        job = new UnprocessedAuditProcessorJob(unprocessedEventStorage, eventService);

        ReflectionTestUtils.setField(job, "systemService", systemService);
        ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
        ReflectionTestUtils.setField(job, "clock", clock);
        ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
        ReflectionTestUtils.setField(job, "systemAttributeRepository", systemAttributeRepository);

        addAppender(UnprocessedAuditProcessorJob.class);
    }

    @Test
    void run_whenSystemIsNotReady_thenSkipExecution() {
        // given
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP)));

        // when
        job.run();

        // then
        assertTrue(logAppender.list.isEmpty());
        verify(systemAttributeRepository).getSystemStatus();
    }

    @Test
    void run_whenJobIsDisabled_thenSkipExecution() {
        // given
        when(systemPropertyService.getBoolean(SystemProperty.UNPROCESSED_AUDIT_LOGS_ENABLED)).thenReturn(false);
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        // when
        job.run();

        // then
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.UNPROCESSED_AUDIT_LOGS_ENABLED);
    }

    @Test
    void run_whenNoUnprocessedEventsAvailable_thenSkipProcessing() {
        // given
        when(systemPropertyService.getBoolean(SystemProperty.UNPROCESSED_AUDIT_LOGS_ENABLED)).thenReturn(true);
        when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
        when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));
        when(unprocessedEventStorage.getAll(true)).thenReturn(emptyList());

        // when
        job.run();

        // then
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "Number of unprocessed events: 0");
        verify(jobRepository, times(2)).save(any(JobEntity.class));
        verify(jobRepository).findById(anyLong());
    }

    @Test
    void run_whenAllConditionsMet_thenProcess() {
        // given
        when(systemPropertyService.getBoolean(SystemProperty.UNPROCESSED_AUDIT_LOGS_ENABLED)).thenReturn(true);
        when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
        when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));
        when(unprocessedEventStorage.getAll(true)).thenReturn(List.of(
                UserEvent.builder().userId(1L).build()
        ));

        // when
        job.run();

        // then
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "Number of unprocessed events: 1");
        verify(jobRepository, times(2)).save(any(JobEntity.class));
        verify(jobRepository).findById(anyLong());
    }
}

