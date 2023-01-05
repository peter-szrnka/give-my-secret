package io.github.gms.secure.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
 * Unit test of {@link SecretRotationJob}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationJobTest extends AbstractLoggingUnitTest {

	@Mock
	private SecretRepository secretRepository;

	@Mock
	private SecretRotationService service;

	@InjectMocks
	private SecretRotationJob job;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		((Logger) LoggerFactory.getLogger(SecretRotationJob.class)).addAppender(logAppender);
		setupClock();
	}

	@Test
	void shouldNotProcess() {
		// arrange
		when(secretRepository.findAllOldRotated(any(LocalDateTime.class)))
			.thenReturn(Lists.newArrayList());

		// act
		job.execute();

		// assert
		verify(service, never()).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(LocalDateTime.class));
		assertTrue(logAppender.list.isEmpty());
	}
	
	@Test
	void shouldProcess() {
		// arrange
		when(secretRepository.findAllOldRotated(any(LocalDateTime.class)))
			.thenReturn(Lists.newArrayList(
					TestUtils.createSecretEntity(), 
					TestUtils.createSecretEntity(RotationPeriod.HOURLY, LocalDateTime.now().minusDays(1l)),
					TestUtils.createSecretEntity(RotationPeriod.MONTHLY, LocalDateTime.now().minusDays(1l))
			));

		// act
		job.execute();

		// assert
		verify(service).rotateSecret(any(SecretEntity.class));
		verify(secretRepository).findAllOldRotated(any(LocalDateTime.class));
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 entities updated", logAppender.list.get(0).getFormattedMessage());
	}
}
