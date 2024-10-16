package io.github.gms.job;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.keystore.KeystoreFileService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GeneratedKeystoreCleanupJobTest extends AbstractLoggingUnitTest {

    private SystemService systemService;
    private SystemPropertyService systemPropertyService;
    private KeystoreFileService service;
    private GeneratedKeystoreCleanupJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        systemService = mock(SystemService.class);
        systemPropertyService = mock(SystemPropertyService.class);
        service = mock(KeystoreFileService.class);
        job = new GeneratedKeystoreCleanupJob(systemService, systemPropertyService, service);

        addAppender(GeneratedKeystoreCleanupJob.class);
    }

    @Test
    void execute_whenJobIsDisabled_thenSkipExecution() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED)).thenReturn(false);

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemPropertyService).getBoolean(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED);
    }

    @Test
    void execute_whenSkipJobExecutionReturnsTrue_thenSkipExecution() {
        // arrange
        when(systemService.getContainerId()).thenReturn("ab123457");
        when(systemPropertyService.getBoolean(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED)).thenReturn(true);
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE)).thenReturn(true);
        when(systemPropertyService.get(SystemProperty.KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID)).thenReturn("ab123456");

        // act
        job.execute();

        // assert
        assertTrue(logAppender.list.isEmpty());
        verify(systemService).getContainerId();
        verify(systemPropertyService).getBoolean(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED);
        verify(systemPropertyService).get(SystemProperty.KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID);
        verify(service, never()).deleteTempKeystoreFiles();
    }

    @Test
    void shouldNotProcess() {
        // arrange
        when(systemPropertyService.getBoolean(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED)).thenReturn(true);
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
        when(systemPropertyService.getBoolean(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED)).thenReturn(true);
        when(service.deleteTempKeystoreFiles()).thenReturn(1L);

        // act
        job.execute();

        // assert
        assertFalse(logAppender.list.isEmpty());
        assertLogContains(logAppender, "1 temporary keystore(s) deleted");
        verify(service).deleteTempKeystoreFiles();
    }
}
