package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.ldap.LdapSyncService;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createJobEntity;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserSyncJobTest extends AbstractLoggingUnitTest {

    private SystemService systemService;
    private SystemPropertyService systemPropertyService;
    private LdapSyncService service;
    private Clock clock;
    private JobRepository jobRepository;
    private SystemAttributeRepository systemAttributeRepository;
    private LdapUserSyncJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        service = mock(LdapSyncService.class);
        clock = mock(Clock.class);
        jobRepository = mock(JobRepository.class);
        systemAttributeRepository = mock(SystemAttributeRepository.class);
        job = new LdapUserSyncJob(service);

        ReflectionTestUtils.setField(job, "systemService", systemService);
        ReflectionTestUtils.setField(job, "systemPropertyService", systemPropertyService);
        ReflectionTestUtils.setField(job, "clock", clock);
        ReflectionTestUtils.setField(job, "jobRepository", jobRepository);
        ReflectionTestUtils.setField(job, "systemAttributeRepository", systemAttributeRepository);

        addAppender(LdapUserSyncJob.class);
    }

    @Test
    void run_whenSystemIsNotReady_thenSkipExecution() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP)));

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemAttributeRepository).getSystemStatus();
    }

    @Test
    void run_whenJobIsDisabled_thenSkipExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED)).thenReturn(false);
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED);
    }

    @Test
    void run_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
        // arrange
        when(systemService.getContainerId()).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.LDAP_SYNC_RUNNER_CONTAINER_ID)).thenReturn("ab123456");
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        // act
        job.run();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).get(SystemProperty.LDAP_SYNC_RUNNER_CONTAINER_ID);
        verify(service, never()).synchronizeUsers();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void shouldProcess(int deletedUserCount) {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED)).thenReturn(true);
        when(service.synchronizeUsers()).thenReturn(Pair.of(2, deletedUserCount));
        when(jobRepository.save(any(JobEntity.class))).thenReturn(createJobEntity());
        when(jobRepository.findById(anyLong())).thenReturn(java.util.Optional.of(createJobEntity()));
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        // act
        job.run();

        // assert
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "2 user(s) synchronized and " + deletedUserCount + " of them ha" + (deletedUserCount == 1 ? "s" : "ve")
                + " been marked as deletable.");
        verify(service).synchronizeUsers();
        verify(jobRepository, times(2)).save(any(JobEntity.class));
        verify(jobRepository).findById(anyLong());
    }
}
