package io.github.gms.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.keystore.KeystoreFileService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GeneratedKeystoreCleanupJobTest extends AbstractLoggingUnitTest {

    private Environment env;
    private SystemPropertyService systemPropertyService;
    private KeystoreFileService service;
    private GeneratedKeystoreCleanupJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        env = mock(Environment.class);
        systemPropertyService = mock(SystemPropertyService.class);
        service = mock(KeystoreFileService.class);
        job = new GeneratedKeystoreCleanupJob(env, systemPropertyService, service);

        ((Logger) LoggerFactory.getLogger(GeneratedKeystoreCleanupJob.class)).addAppender(logAppender);
    }

    @Test
    void execute_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
        // arrange
        when(env.getProperty("HOSTNAME")).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).get(SystemProperty.KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID);
        verify(service, never()).deleteTempKeystoreFiles();
    }

    @Test
    void shouldNotProcess() {
        // arrange
        when(service.deleteTempKeystoreFiles()).thenReturn(0L);

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(service).deleteTempKeystoreFiles();
    }

    @Test
    void shouldProcess() {
        // arrange
        when(service.deleteTempKeystoreFiles()).thenReturn(1L);

        // act
        job.execute();

        // assert
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "1 temporary keystore(s) deleted");
        verify(service).deleteTempKeystoreFiles();
    }
}
