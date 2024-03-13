package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.ldap.LdapSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserSyncJobTest extends AbstractLoggingUnitTest {

    private LdapSyncService service;
    private LdapUserSyncJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        service = mock(LdapSyncService.class);
        job = new LdapUserSyncJob(service);

        ((Logger) LoggerFactory.getLogger(LdapUserSyncJob.class)).addAppender(logAppender);
    }

    @Test
    void shouldProcess() {
        // arrange
        when(service.synchronizeUsers()).thenReturn(2);

        // act
        job.execute();

        // assert
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "2 users(s) synchronized");
        verify(service).synchronizeUsers();
    }
}
