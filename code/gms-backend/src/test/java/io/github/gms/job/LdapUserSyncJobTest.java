package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.ldap.LdapSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

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

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void shouldProcess(int deletedUserCount) {
        // arrange
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
