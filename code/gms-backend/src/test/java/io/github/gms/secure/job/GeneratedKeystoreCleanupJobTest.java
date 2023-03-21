package io.github.gms.secure.job;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.secure.repository.EventRepository;
import io.github.gms.secure.service.KeystoreFileService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link GeneratedKeystoreCleanupJob}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class GeneratedKeystoreCleanupJobTest  extends AbstractLoggingUnitTest  {

    @Mock
    private KeystoreFileService service;

    @InjectMocks
    private GeneratedKeystoreCleanupJob job;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
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
