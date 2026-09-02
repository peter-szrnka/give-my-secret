package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.*;
import io.github.gms.common.service.GmsThreadLocalValues;
import io.github.gms.common.types.EventSource;
import io.github.gms.common.util.Constants;
import io.github.gms.common.util.ThreadLocalContext;
import io.github.gms.functions.event.EventRepository;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

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
	private SystemAttributeRepository systemAttributeRepository;
	
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
		systemAttributeRepository = mock(SystemAttributeRepository.class);
		job = new EventMaintenanceJob(eventRepository);
		ReflectionTestUtils.setField(job, "systemService", systemService);
		ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
		ReflectionTestUtils.setField(job, "clock", clock);
		ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
		ReflectionTestUtils.setField(job, "systemAttributeRepository", systemAttributeRepository);
		addAppender(EventMaintenanceJob.class);
	}

	@Test
	void run_whenSystemIsNotReady_thenSkipExecution() {
		// given
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP)));

		// when
		job.run();

		// then
		assertTrue(logAppender.list.isEmpty());
		verify(systemAttributeRepository).getSystemStatus();
	}

	@Test
	void run_whenJobIsDisabled_thenSkipExecution() {
		// given
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(false);
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// when
		job.run();

		// then
		assertTrue(logAppender.list.isEmpty());
		verify(systemPropertyService).getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED);
	}

	@Test
	void run_whenNoEventsDeleted_thenSkipLogging() {
		// given
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenReturn(0);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// when
		job.run();
		
		// then
		assertTrue(logAppender.list.isEmpty());

		ArgumentCaptor<ZonedDateTime> dateCArgumentCaptor = ArgumentCaptor.forClass(ZonedDateTime.class);
		verify(eventRepository).deleteAllEventDateOlderThan(dateCArgumentCaptor.capture());
		assertEquals("2023-06-28T00:00Z", dateCArgumentCaptor.getValue().toString());
		verify(systemPropertyService).get(SystemProperty.JOB_OLD_EVENT_LIMIT);
		verify(jobRepository, times(2)).save(any(JobEntity.class));
		verify(jobRepository).findById(anyLong());
	}

	@Test
	void run_whenAllConditionsMet_thenProcess() {
		// given
		when(systemPropertyService.getBoolean(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED)).thenReturn(true);
		when(systemPropertyService.get(SystemProperty.JOB_OLD_EVENT_LIMIT)).thenReturn("1;d");
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MockedStatic<TimeUnit> mockedTimeUnit = mockStatic(TimeUnit.class);
		mockedTimeUnit.when(() -> TimeUnit.getByCode("d")).thenReturn(TimeUnit.DAY);
		when(eventRepository.deleteAllEventDateOlderThan(any(ZonedDateTime.class))).thenAnswer(new Answer<>() {
			@Override
			public Integer answer(InvocationOnMock invocation) {
				assertEquals(EventSource.JOB, GmsThreadLocalValues.getEventSource());
				assertEquals(Constants.JOB_USER, GmsThreadLocalValues.getUserId());
				assertNotNull(ThreadLocalContext.get(MdcParameter.JOB_ID));
				assertNotNull(ThreadLocalContext.get(MdcParameter.CORRELATION_ID));
				return 1;
			}
		});
		when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
		when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
		when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

		// when
		job.run();
		
		// then
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
		ArgumentCaptor<JobEntity> jobEntityArgumentCaptor = ArgumentCaptor.forClass(JobEntity.class);
		verify(jobRepository, times(2)).save(jobEntityArgumentCaptor.capture());
		JobEntity savedJobEntity = jobEntityArgumentCaptor.getAllValues().get(1);
		assertEquals(JobStatus.COMPLETED, savedJobEntity.getStatus());
		assertNotNull(savedJobEntity.getEndTime());
		assertEquals("test", savedJobEntity.getMessage());
		verify(jobRepository).findById(anyLong());
		assertNull(GmsThreadLocalValues.getEventSource());
		assertNull(GmsThreadLocalValues.getUserId());
		assertNull(ThreadLocalContext.get(MdcParameter.JOB_ID));
		assertNull(ThreadLocalContext.get(MdcParameter.CORRELATION_ID));
	}
}

