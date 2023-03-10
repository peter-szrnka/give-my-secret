package io.github.gms.secure.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.secure.repository.MessageRepository;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link MessageCleanupJob}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
public class MessageCleanupJobTest extends AbstractLoggingUnitTest {

	@Mock
	private Clock clock;
	@Mock
	private MessageRepository messageRepository;

	@InjectMocks
	private MessageCleanupJob job;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		ReflectionTestUtils.setField(job, "oldMessageLimit", "1;d");
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
