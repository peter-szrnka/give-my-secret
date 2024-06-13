package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.functions.gdpr.UserAssetDeletionService;
import io.github.gms.functions.gdpr.UserDeletionService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Set;

import static io.github.gms.util.TestUtils.assertLogContains;
import static io.github.gms.util.TestUtils.assertLogMissing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserDeletionJobTest extends AbstractLoggingUnitTest {

    private Environment env;
    private SystemPropertyService systemPropertyService;
    private UserDeletionService userDeletionService;
    private UserAssetDeletionService userAssetDeletionService;
    private UserDeletionJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        env = mock(Environment.class);
        systemPropertyService = mock(SystemPropertyService.class);
        userDeletionService = mock(UserDeletionService.class);
        userAssetDeletionService = mock(UserAssetDeletionService.class);
        job = new UserDeletionJob(env, systemPropertyService, userDeletionService, userAssetDeletionService);
        ((Logger) LoggerFactory.getLogger(UserDeletionJob.class)).addAppender(logAppender);
    }

    @Test
    void shouldSkipProcessing() {
        // arrange
        when(userDeletionService.getRequestedUserDeletionIds()).thenReturn(Collections.emptySet());

        // act
        job.execute();

        // assert
        verify(userDeletionService).getRequestedUserDeletionIds();
        assertLogMissing(logAppender, "Deleting requested user assets(API keys, secrets, keystore resources,etc.");
    }

    @Test
    void shouldProcess() {
        // arrange
        Set<Long> userIds = Set.of(1L, 2L);
        when(userDeletionService.getRequestedUserDeletionIds()).thenReturn(userIds);

        // act
        job.execute();

        // assert
        verify(userDeletionService).getRequestedUserDeletionIds();
        verify(userAssetDeletionService).executeRequestedUserAssetDeletion(userIds);
        verify(userDeletionService).executeRequestedUserDeletion(userIds);
        assertLogContains(logAppender, "2 user(s) requested to delete");
        assertLogContains(logAppender, "Deleting requested user assets(API keys, secrets, keystore resources,etc.");
        assertLogContains(logAppender, "Deleting requested users");
    }
}
