package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static io.github.gms.util.TestUtils.createJobEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageCleanupJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private JobRepository jobRepository;
	private SystemService systemService;
	private MessageRepository messageRepository;
	private MessageCleanupJob job;
	private SystemPropertyService systemPropertyService;
	private SystemAttributeRepository systemAttributeRepository;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		clock = mock(Clock.class);
		jobRepository = mock(JobRepository.class);
		messageRepository = mock(MessageRepository.class);
		systemService = mock(SystemService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		systemAttributeRepository = mock(SystemAttributeRepository.class);
		job = new MessageCleanupJob(messageRepository);

		ReflectionTestUtils.setField(job, "systemService", systemService);
		ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
		ReflectionTestUtils.setField(job, "clock", clock);
		ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
		ReflectionTestUtils.setField(job, "systemAttributeRepository", systemAttributeRepository);

		addAppender(MessageCleanupJob.class);

		MDC.clear();
	}

	@Test
	void run_whenSystemIsNotReady_thenSkipExecution() {
		// arrange
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP)));

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemAttributeRepository).getSystemStatus();
	}

	@Test
	void run_whenJobIsDisabled_thenSkipExecution() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED)).thenReturn(false);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemPropertyService).getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED);
	}

	@Test
	void run_whenAppIsNotRunningInMainContainer_thenSkipExecution() {
		// arrange
		when(systemService.getContainerId()).thenReturn("ab123457");
		when(systemPropertyService.getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn("ab123456");
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemService).getContainerId();
		verify(systemPropertyService).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED);
	}
	
	@Test
	void run_whenNoMessagesDeleted_thenSkipLogging() {
		// arrange
		setupClock(clock);
		when(messageRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(systemPropertyService.getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_MESSAGE_LIMIT)).thenReturn("1;d");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(false);
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(messageRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_MESSAGE_LIMIT);
		verify(systemPropertyService, never()).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED);
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}
	
	@Test
	void run_whenAllConditionsMet_thenProcess() {
		// arrange
		setupClock(clock);
		when(messageRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(1);
		when(systemPropertyService.getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_MESSAGE_LIMIT)).thenReturn("1;d");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(false);
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 message(s) deleted", logAppender.list.getFirst().getFormattedMessage());
		verify(messageRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_MESSAGE_LIMIT);
		verify(systemPropertyService, never()).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).getBoolean(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED);
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}
}
