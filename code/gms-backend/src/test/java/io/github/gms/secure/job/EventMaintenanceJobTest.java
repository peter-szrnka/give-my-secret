package io.github.gms.secure.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.secure.repository.EventRepository;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link EventMaintenanceJob}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EventMaintenanceJobTest extends AbstractLoggingUnitTest {
	
	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventMaintenanceJob job;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		ReflectionTestUtils.setField(job, "oldEventLimit", "1;d");
		((Logger) LoggerFactory.getLogger(EventMaintenanceJob.class)).addAppender(logAppender);
		
		setupClock();
	}
	
	@Test
	void shouldNotProcess() {
		// arrange
		when(eventRepository.findAllEventDateOlderThan(any(LocalDateTime.class))).thenReturn(Lists.newArrayList());
		
		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(eventRepository).findAllEventDateOlderThan(any(LocalDateTime.class));
		verify(eventRepository).deleteAll(anyList());
	}
	
	@Test
	void shouldProcess() {
		// arrange
		when(eventRepository.findAllEventDateOlderThan(any(LocalDateTime.class))).thenReturn(Lists.newArrayList(TestUtils.createEventEntity()));
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 event(s) deleted", logAppender.list.get(0).getFormattedMessage());
		verify(eventRepository).findAllEventDateOlderThan(any(LocalDateTime.class));
		verify(eventRepository).deleteAll(anyList());
	}
}
