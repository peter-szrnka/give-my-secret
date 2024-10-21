package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.TimeUnit;
import io.github.gms.functions.event.EventRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static io.github.gms.util.TestUtils.createJobEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventMaintenanceJobTest extends AbstractLoggingUnitTest {

	private SystemService systemService;
    private EventRepository eventRepository;
	private EventMaintenanceJob job;
	private Clock clock;
	private SystemPropertyService systemPropertyService;
	private JobRepository jobRepository;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		// init
        clock = mock(Clock.class);
		systemService = mock(SystemService.class);
		eventRepository = mock(EventRepository.class);
		systemPropertyService = mock(SystemPropertyService.class);
		jobRepository = mock(JobRepository.class);
		job = new EventMaintenanceJob(eventRepository);
		ReflectionTestUtils.setField(job, "systemService", systemService);
		ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
		ReflectionTestUtils.setField(job, "clock", clock);
		ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
		addAppender(EventMaintenanceJob.class);
	}

	@Test
	void run_whenJobIsDisabled_thenSkipExecution() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(false);

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemPropertyService).getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED);
	}

	@Test
	void run_whenAppIsNotRunningInMainContainer_thenSkipExecution() {
		// arrange
		when(systemService.getContainerId()).thenReturn("ab123457");
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());
		verify(systemService).getContainerId();
		verify(systemPropertyService).get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
	}

	@Test
	void shouldNotProcess() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn(null);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));

		// act
		job.run();
		
		// assert
		assertTrue(logAppender.list.isEmpty());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).deleteAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		verify(systemPropertyService).get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_EVENT_LIMIT);
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}

	@Test
	void run_whenMultiNodeDisabled_thenShouldNotProcess() {
		// arrange
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(false);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));

		// act
		job.run();

		// assert
		assertTrue(logAppender.list.isEmpty());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).deleteAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		verify(systemPropertyService, never()).get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_EVENT_LIMIT);
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}

	@Test
	void shouldProcess() {
		// arrange
		when(systemService.getContainerId()).thenReturn("ab123456");
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)).thenReturn("ab123456");
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MockedStatic<TimeUnit> mockedTimeUnit = mockStatic(TimeUnit.class);
		mockedTimeUnit.when(() -> TimeUnit.getByCode("d")).thenReturn(TimeUnit.DAY);
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(1);
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));

		// act
		job.run();
		
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
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}
}
