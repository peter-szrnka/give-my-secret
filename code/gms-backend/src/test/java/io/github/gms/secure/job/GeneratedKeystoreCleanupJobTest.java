package io.github.gms.secure.job;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.secure.service.KeystoreFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link GeneratedKeystoreCleanupJob}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class GeneratedKeystoreCleanupJobTest  extends AbstractLoggingUnitTest  {

    private KeystoreFileService service;
    private GeneratedKeystoreCleanupJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // init
        service = mock(KeystoreFileService.class);
        job = new GeneratedKeystoreCleanupJob(service);

        ((Logger) LoggerFactory.getLogger(GeneratedKeystoreCleanupJob.class)).addAppender(logAppender);
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
        when(service.deleteTempKeystoreFiles()).thenReturn(3L);

        // act
        job.execute();

        // assert
        assertFalse(logAppender.list.isEmpty());
        assertEquals("3 temporary keystore(s) deleted", logAppender.list.get(0).getFormattedMessage());
        verify(service).deleteTempKeystoreFiles();
    }
}
