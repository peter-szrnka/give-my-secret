package io.github.gms.job;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.secret.SecretRotationService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private SystemService systemService;
	private SystemPropertyService systemPropertyService;
	private SecretRepository secretRepository;
	private SecretRotationService service;
	private SecretRotationJob job;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		clock = mock(Clock.class);
		systemService = mock(SystemService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		secretRepository = mock(SecretRepository.class);
		service = mock(SecretRotationService.class);
		job = new SecretRotationJob(systemService, systemPropertyService, clock, secretRepository, service);
		addAppender(SecretRotationJob.class);
	}

	@Test
	void execute_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
		// arrange
		when(systemService.getContainerId()).thenReturn("ab123457");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.SECRET_ROTATION_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

		// act
		job.execute();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemPropertyService).get(SystemProperty.SECRET_ROTATION_RUNNER_CONTAINER_ID);
		verify(service, never()).rotateSecret(any(SecretEntity.class));
	}

	@Test
	void shouldNotProcess() {
		// arrange
		Clock mockClock = Clock.fixed(Instant.parse("2023-06-29T00:00:00Z"), ZoneId.systemDefault());
		when(secretRepository.findAllOldRotated(any(ZonedDateTime.class)))
			.thenReturn(Lists.newArrayList(TestUtils.createSecretEntity(RotationPeriod.MONTHLY, ZonedDateTime.now(mockClock).minusDays(10L))));
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		// act
		job.execute();

		// assert
		verify(service, never()).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(ZonedDateTime.class));
		assertTrue(logAppender.list.isEmpty());
	}
	
	@Test
	void shouldProcess() {
		// arrange
		Clock mockClock = Clock.fixed(Instant.parse("2023-06-29T00:00:00Z"), ZoneId.systemDefault());
		when(secretRepository.findAllOldRotated(any(ZonedDateTime.class)))
			.thenReturn(Lists.newArrayList(
					TestUtils.createSecretEntity(RotationPeriod.HOURLY, ZonedDateTime.now(mockClock).minusDays(10L)), 
					TestUtils.createSecretEntity(RotationPeriod.MONTHLY, ZonedDateTime.now(mockClock).minusDays(10L))
			));
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		// act
		job.execute();

		// assert
		verify(service, times(1)).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(ZonedDateTime.class));
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 entities updated", logAppender.list.getFirst().getFormattedMessage());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(secretRepository).findAllOldRotated(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T23:59:05Z", dateCArgumentCaptor.getValue().toString());
	}
}
