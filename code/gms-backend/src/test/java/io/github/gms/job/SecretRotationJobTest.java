package io.github.gms.job;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.secret.SecretRotationService;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.util.Optional;

import static io.github.gms.util.TestUtils.createJobEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private JobRepository jobRepository;
	private SystemService systemService;
	private SystemPropertyService systemPropertyService;
	private SecretRepository secretRepository;
	private SecretRotationService service;
	private SystemAttributeRepository systemAttributeRepository;
	private SecretRotationJob job;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		clock = mock(Clock.class);
		jobRepository = mock(JobRepository.class);
		systemService = mock(SystemService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		secretRepository = mock(SecretRepository.class);
		service = mock(SecretRotationService.class);
		systemAttributeRepository = mock(SystemAttributeRepository.class);
		job = new SecretRotationJob(secretRepository, service);

		ReflectionTestUtils.setField(job, "systemService", systemService);
		ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
		ReflectionTestUtils.setField(job, "clock", clock);
		ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
		ReflectionTestUtils.setField(job, "systemAttributeRepository", systemAttributeRepository);

		addAppender(SecretRotationJob.class);
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
		when(systemPropertyService.getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED)).thenReturn(false);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemPropertyService).getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED);
	}

	@Test
	void run_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
		// arrange
		when(systemService.getContainerId()).thenReturn("ab123457");
		when(systemPropertyService.getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.SECRET_ROTATION_RUNNER_CONTAINER_ID)).thenReturn("ab123456");
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemPropertyService).get(SystemProperty.SECRET_ROTATION_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED);
		verify(service, never()).rotateSecret(any(SecretEntity.class));
	}

	@Test
	void shouldNotProcess() { // TODO: Rename this method to something more descriptive
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED)).thenReturn(true);
		Clock mockClock = Clock.fixed(Instant.parse("2023-06-29T00:00:00Z"), ZoneId.systemDefault());
		when(secretRepository.findAllOldRotated(any(ZonedDateTime.class)))
			.thenReturn(Lists.newArrayList(TestUtils.createSecretEntity(RotationPeriod.MONTHLY, ZonedDateTime.now(mockClock).minusDays(10L))));
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();

		// assert
		verify(service, never()).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(ZonedDateTime.class));
		verify(systemPropertyService).getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED);
		assertTrue(logAppender.list.isEmpty());
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}
	
	@Test
	void run_whenAllConditionsMet_thenProcess() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED)).thenReturn(true);
		Clock mockClock = Clock.fixed(Instant.parse("2023-06-29T00:00:00Z"), ZoneId.systemDefault());
		when(secretRepository.findAllOldRotated(any(ZonedDateTime.class)))
			.thenReturn(Lists.newArrayList(
					TestUtils.createSecretEntity(RotationPeriod.HOURLY, ZonedDateTime.now(mockClock).minusDays(10L)), 
					TestUtils.createSecretEntity(RotationPeriod.MONTHLY, ZonedDateTime.now(mockClock).minusDays(10L))
			));
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// act
		job.run();

		// assert
		verify(service, times(1)).rotateSecret(any(SecretEntity.class));
		verify(systemPropertyService).getBoolean(SystemProperty.SECRET_ROTATION_JOB_ENABLED);
		verify(secretRepository).findAllOldRotated(any(ZonedDateTime.class));
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 entities updated", logAppender.list.getFirst().getFormattedMessage());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(secretRepository).findAllOldRotated(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T23:59:05Z", dateCArgumentCaptor.getValue().toString());
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}
}
