package io.github.gms.secure.job;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.SecretRotationService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link SecretRotationJob}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationJobTest extends AbstractLoggingUnitTest {

	private SecretRepository secretRepository;
	private SecretRotationService service;
	private SecretRotationJob job;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		Clock clock = mock(Clock.class);
		secretRepository = mock(SecretRepository.class);
		service = mock(SecretRotationService.class);
		job = new SecretRotationJob(clock, secretRepository, service);
		((Logger) LoggerFactory.getLogger(SecretRotationJob.class)).addAppender(logAppender);
		setupClock(clock);
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
		when(secretRepository.findAllOldRotated(any(ZonedDateTime.class)))
			.thenReturn(Lists.newArrayList(
					TestUtils.createSecretEntity(), 
					TestUtils.createSecretEntity(RotationPeriod.HOURLY, ZonedDateTime.now().minusDays(1L)),
					TestUtils.createSecretEntity(RotationPeriod.MONTHLY, ZonedDateTime.now().minusDays(1L))
			));

		// act
		job.execute();

		// assert
		verify(service).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(ZonedDateTime.class));
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 entities updated", logAppender.list.get(0).getFormattedMessage());
	}
}
