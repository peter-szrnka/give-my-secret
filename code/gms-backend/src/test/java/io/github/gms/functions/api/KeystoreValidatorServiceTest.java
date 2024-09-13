package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.keystore.KeystoreRepository;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link KeystoreValidatorService}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreValidatorServiceTest extends AbstractLoggingUnitTest {

    private KeystoreRepository keystoreRepository;
    private KeystoreAliasRepository keystoreAliasRepository;
    private KeystoreValidatorService service;

    @Override
    @BeforeEach
    public void setup() {
        keystoreRepository = mock(KeystoreRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
        service = new KeystoreValidatorService(keystoreRepository, keystoreAliasRepository);

        addAppender(KeystoreValidatorService.class);
    }

    @Test
    void shouldKeystoreAliasMissing() {
        // arrange
        when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.empty());
        SecretEntity secretEntity = TestUtils.createSecretEntity();

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.validateSecretKeystore(secretEntity));
        assertEquals("Keystore alias is not available!", exception.getMessage());

        assertLogContains(logAppender, "Keystore alias not found");
        verify(keystoreAliasRepository).findById(anyLong());
    }

    @Test
    void shouldKeystoreMissing() {
        // arrange
        when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
        when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.empty());
        SecretEntity secretEntity = TestUtils.createSecretEntity();

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.validateSecretKeystore(secretEntity));
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
        SecretEntity secretEntity = TestUtils.createSecretEntity();

        // act
        assertDoesNotThrow(() -> service.validateSecretKeystore(secretEntity));

        // assert
        verify(keystoreAliasRepository).findById(anyLong());
        verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
    }
}
