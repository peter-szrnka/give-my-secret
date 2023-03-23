package io.github.gms.secure.job;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.secure.repository.EventRepository;
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
class EventMaintenanceJobTest extends AbstractLoggingUnitTest {

	private EventRepository eventRepository;
	private EventMaintenanceJob job;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		// init
		Clock clock = mock(Clock.class);
		eventRepository = mock(EventRepository.class);
		job = new EventMaintenanceJob(clock, eventRepository, "1;d");
		((Logger) LoggerFactory.getLogger(EventMaintenanceJob.class)).addAppender(logAppender);
		setupClock(clock);
	}
	
	@Test
	void shouldNotProcess() {
		// arrange
		when(eventRepository.findAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(Lists.newArrayList());
		
		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(eventRepository).findAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(eventRepository).deleteAll(anyList());
	}
	
	@Test
	void shouldProcess() {
		// arrange
		when(eventRepository.findAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(Lists.newArrayList(TestUtils.createEventEntity()));
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 event(s) deleted", logAppender.list.get(0).getFormattedMessage());
		verify(eventRepository).findAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(eventRepository).deleteAll(anyList());
	}
}
