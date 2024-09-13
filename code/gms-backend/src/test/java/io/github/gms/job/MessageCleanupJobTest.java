package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageCleanupJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private SystemService systemService;
	private MessageRepository messageRepository;
	private MessageCleanupJob job;
	private SystemPropertyService systemPropertyService;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		clock = mock(Clock.class);
		messageRepository = mock(MessageRepository.class);
		systemService = mock(SystemService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		job = new MessageCleanupJob(systemService, clock, messageRepository, systemPropertyService);
		addAppender(MessageCleanupJob.class);
	}

	@Test
	void execute_whenAppIsNotRunningInMainContainer_thenSkipExecution() {
		// arrange
		when(systemService.getContainerId()).thenReturn("ab123457");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

		// act
		job.execute();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemService).getContainerId();
		verify(systemPropertyService).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}
	
	@Test
	void shouldNotProcess() {
		// arrange
		setupClock(clock);
		when(messageRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_MESSAGE_LIMIT)).thenReturn("1;d");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(false);

		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(messageRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_MESSAGE_LIMIT);
		verify(systemPropertyService, never()).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}
	
	@Test
	void shouldProcess() {
		// arrange
		setupClock(clock);
		when(messageRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(1);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_MESSAGE_LIMIT)).thenReturn("1;d");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(false);
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 message(s) deleted", logAppender.list.getFirst().getFormattedMessage());
		verify(messageRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_MESSAGE_LIMIT);
		verify(systemPropertyService, never()).get(SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}
}
