package io.github.gms.secure.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.SecretRotationService;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private SecretRepository secretRepository;
	private SecretRotationService service;
	private SecretRotationJob job;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		clock = mock(Clock.class);
		secretRepository = mock(SecretRepository.class);
		service = mock(SecretRotationService.class);
		job = new SecretRotationJob(clock, secretRepository, service);
		((Logger) LoggerFactory.getLogger(SecretRotationJob.class)).addAppender(logAppender);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
	}

	@Test
	void shouldNotProcess() {
		// arrange
		when(secretRepository.findAllOldRotated(any(ZonedDateTime.class)))
			.thenReturn(Lists.newArrayList());

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

		// act
		job.execute();

		// assert
		verify(service, times(1)).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(ZonedDateTime.class));
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 entities updated", logAppender.list.get(0).getFormattedMessage());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(secretRepository).findAllOldRotated(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T23:59:05Z", dateCArgumentCaptor.getValue().toString());
	}
}
