package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.ldap.LdapSyncService;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.util.Pair;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
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
    private LdapUserSyncJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        service = mock(LdapSyncService.class);
        job = new LdapUserSyncJob(systemService, systemPropertyService, service);

        addAppender(LdapUserSyncJob.class);
    }

    @Test
    void execute_whenJobIsDisabled_thenSkipExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED)).thenReturn(false);

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED);
    }

    @Test
    void execute_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
        // arrange
        when(systemService.getContainerId()).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.LDAP_SYNC_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.LDAP_SYNC_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

        // act
        job.execute();

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

        // act
        job.execute();

        // assert
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "2 user(s) synchronized and " + deletedUserCount + " of them ha" + (deletedUserCount == 1 ? "s" : "ve")
                + " been marked as deletable.");
        verify(service).synchronizeUsers();
    }
}
