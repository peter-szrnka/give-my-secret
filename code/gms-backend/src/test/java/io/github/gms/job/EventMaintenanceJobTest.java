package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.TimeUnit;
import io.github.gms.functions.event.EventRepository;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventMaintenanceJobTest extends AbstractLoggingUnitTest {

	private Environment env;
    private EventRepository eventRepository;
	private EventMaintenanceJob job;
	private Clock clock;
	private SystemPropertyService systemPropertyService;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		// init
        clock = mock(Clock.class);
		env = mock(Environment.class);
		eventRepository = mock(EventRepository.class);
		systemPropertyService = mock(SystemPropertyService.class);
		job = new EventMaintenanceJob(env, clock, eventRepository, systemPropertyService);
		((Logger) LoggerFactory.getLogger(EventMaintenanceJob.class)).addAppender(logAppender);
	}

	@Test
	void execute_whenAppIsNotRunningInMainContainer_thenSkipExecution() {
		// arrange
		when(env.getProperty("HOSTNAME")).thenReturn("ab123457");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

		// act
		job.execute();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(env).getProperty("HOSTNAME");
		verify(systemPropertyService/*, times(2)*/).get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
	}

	@Test
	void shouldNotProcess() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn(null);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		
		// act
		job.execute();
		
		// assert
		assertTrue(logAppender.list.isEmpty());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).deleteAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		verify(systemPropertyService).get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_EVENT_LIMIT);
	}

	@Test
	void execute_whenMultiNodeDisabled_thenShouldNotProcess() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(false);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		// act
		job.execute();

		// assert
		assertTrue(logAppender.list.isEmpty());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).deleteAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		verify(systemPropertyService, never()).get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_EVENT_LIMIT);
	}

	@Test
	void shouldProcess() {
		// arrange
		when(env.getProperty("HOSTNAME")).thenReturn("ab123456");
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn("ab123456");
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MockedStatic<TimeUnit> mockedTimeUnit = mockStatic(TimeUnit.class);
		mockedTimeUnit.when(() -> TimeUnit.getByCode("d")).thenReturn(TimeUnit.DAY);
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(1);
		
		// act
		job.execute();
		
		// assert
		assertFalse(logAppender.list.isEmpty());
		assertEquals("1 event(s) deleted", logAppender.list.getFirst().getFormattedMessage());
		verify(eventRepository).deleteAllEventDateOlderThan(any(ZonedDateTime.class));

		ArgumentCaptor<String> codeArgumentCaptor = ArgumentCaptor.forClass(String.class);
		mockedTimeUnit.verify(() -> TimeUnit.getByCode(codeArgumentCaptor.capture()));
		assertEquals("d", codeArgumentCaptor.getValue());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).deleteAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		mockedTimeUnit.close();
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_EVENT_LIMIT);
	}
}
