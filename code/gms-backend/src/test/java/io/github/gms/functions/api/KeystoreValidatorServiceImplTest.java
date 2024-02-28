package io.github.gms.functions.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.keystore.KeystoreRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link KeystoreValidatorServiceImpl}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreValidatorServiceImplTest extends AbstractUnitTest {

    private ListAppender<ILoggingEvent> logAppender;
    private KeystoreRepository keystoreRepository;
    private KeystoreAliasRepository keystoreAliasRepository;
    private KeystoreValidatorServiceImpl service;

    @BeforeEach
    void beforeEach() {
        keystoreRepository = mock(KeystoreRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
        service = new KeystoreValidatorServiceImpl(keystoreRepository, keystoreAliasRepository);

        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(KeystoreValidatorServiceImpl.class)).addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logAppender.list.clear();
        logAppender.stop();
    }

    @Test
    void shouldKeystoreAliasMissing() {
        // arrange
        when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.empty());

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.validateSecretKeystore(TestUtils.createSecretEntity()));
        assertEquals("Keystore alias is not available!", exception.getMessage());

        assertLogContains(logAppender, "Keystore alias not found");
        verify(keystoreAliasRepository).findById(anyLong());
    }

    @Test
    void shouldKeystoreMissing() {
        // arrange
        when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
        when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.empty());

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.validateSecretKeystore(TestUtils.createSecretEntity()));
        assertEquals("Invalid keystore!", exception.getMessage());

        assertLogContains(logAppender, "Keystore is not active");
        verify(keystoreAliasRepository).findById(anyLong());
        verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
    }

    @Test
    void shouldSucceed() {
        // arrange
        when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
        when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        // act
        assertDoesNotThrow(() -> service.validateSecretKeystore(TestUtils.createSecretEntity()));

        // assert
        verify(keystoreAliasRepository).findById(anyLong());
        verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
    }
}
