package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.job.MessageCleanupJob;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageCleanupJobTest extends AbstractLoggingUnitTest {

	private MessageRepository messageRepository;
	private MessageCleanupJob job;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		Clock clock = mock(Clock.class);
		messageRepository = mock(MessageRepository.class);
		job = new MessageCleanupJob(clock, messageRepository, "1;d");
		((Logger) LoggerFactory.getLogger(MessageCleanupJob.class)).addAppender(logAppender);
		setupClock(clock);
	}
	
	@Test
	void shouldNotProcess() {
		// arrange
		when(messageRepository.findAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(Lists.newArrayList());
		
		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(messageRepository).findAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(messageRepository).deleteAll(anyList());
	}
	
	@Test
	void shouldProcess() {
		// arrange
		when(messageRepository.findAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(Lists.newArrayList(TestUtils.createMessageEntity()));
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 message(s) deleted", logAppender.list.get(0).getFormattedMessage());
		verify(messageRepository).findAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(messageRepository).deleteAll(anyList());
	}
}
