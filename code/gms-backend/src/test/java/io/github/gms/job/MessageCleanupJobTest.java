package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.time.Clock;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageCleanupJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private Environment env;
	private MessageRepository messageRepository;
	private MessageCleanupJob job;
	private SystemPropertyService systemPropertyService;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		clock = mock(Clock.class);
		messageRepository = mock(MessageRepository.class);
		env = mock(Environment.class);
		systemPropertyService = mock(SystemPropertyService.class);
		job = new MessageCleanupJob(env, clock, messageRepository, systemPropertyService);
		((Logger) LoggerFactory.getLogger(MessageCleanupJob.class)).addAppender(logAppender);
	}

	@Test
	void execute_whenAppIsNotRunningInMainContainer_thenSkipExecution() {
		// arrange
		when(env.getProperty("HOSTNAME")).thenReturn("ab123457");
		when(systemPropertyService.get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

		// act
		job.execute();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(env).getProperty("HOSTNAME");
		verify(systemPropertyService, times(2)).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}
	
	@Test
	void shouldNotProcess() {
		// arrange
		setupClock(clock);
		when(messageRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_MESSAGE_LIMIT)).thenReturn("1;d");
		when(systemPropertyService.get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn(null);
		
		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(messageRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_MESSAGE_LIMIT);
		verify(systemPropertyService).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}
	
	@Test
	void shouldProcess() {
		// arrange
		setupClock(clock);
		when(messageRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(1);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_MESSAGE_LIMIT)).thenReturn("1;d");
		when(systemPropertyService.get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn(null);
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 message(s) deleted", logAppender.list.getFirst().getFormattedMessage());
		verify(messageRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_MESSAGE_LIMIT);
		verify(systemPropertyService).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}
}
