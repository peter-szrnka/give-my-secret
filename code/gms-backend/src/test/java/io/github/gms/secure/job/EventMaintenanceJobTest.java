package io.github.gms.secure.job;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.TimeUnit;
import io.github.gms.secure.repository.EventRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventMaintenanceJobTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private EventRepository eventRepository;
	private EventMaintenanceJob job;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		// init
		clock = mock(Clock.class);
		eventRepository = mock(EventRepository.class);
		job = new EventMaintenanceJob(clock, eventRepository, "1;d");
		((Logger) LoggerFactory.getLogger(EventMaintenanceJob.class)).addAppender(logAppender);
		
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
	}

	@Test
	void shouldNotProcess() {
		// arrange
		when(eventRepository.findAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(Lists.newArrayList());
		
		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(eventRepository).deleteAll(anyList());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).findAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
	}

	@Test
	void shouldProcess() {
		// arrange
		MockedStatic<TimeUnit> mockedTimeUnit = mockStatic(TimeUnit.class);
		mockedTimeUnit.when(() -> TimeUnit.getByCode("d")).thenReturn(TimeUnit.DAY);
		when(eventRepository.findAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(Lists.newArrayList(TestUtils.createEventEntity()));
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 event(s) deleted", logAppender.list.get(0).getFormattedMessage());
		verify(eventRepository).findAllEventDateOlderThan(any(ZonedDateTime.class));
		verify(eventRepository).deleteAll(anyList());

		ArgumentCaptor<String> codeArgumentCaptor = ArgumentCaptor.forClass(String.class);
		mockedTimeUnit.verify(() -> TimeUnit.getByCode(codeArgumentCaptor.capture()));
		assertEquals("d", codeArgumentCaptor.getValue());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).findAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		mockedTimeUnit.close();
	}
}
