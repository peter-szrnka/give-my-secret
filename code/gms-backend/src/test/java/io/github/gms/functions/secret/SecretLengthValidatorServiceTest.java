package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.BooleanValueDto;
import io.github.gms.common.model.GetKeystore;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.keystore.KeystoreDataService;
import io.github.gms.functions.keystore.KeystoreRepository;
import io.github.gms.functions.secret.dto.SecretValueDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Optional;

import static io.github.gms.common.types.ErrorCode.GMS_001;
import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createSecretValueDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretLengthValidatorServiceTest extends AbstractLoggingUnitTest {

    @Mock
    private KeystoreRepository keystoreRepository;
    @Mock
    private KeystoreAliasRepository keystoreAliasRepository;
    @Mock
    private KeystoreDataService keystoreDataService;
    @Mock
    private CryptoService cryptoService;
    @InjectMocks
    private SecretLengthValidatorService secretLengthValidatorService;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        ReflectionTestUtils.setField(secretLengthValidatorService, "keystorePath", "keystorePath");
        addAppender(SecretLengthValidatorService.class);
    }

    @Test
    void validateValueLength_whenKeystoreEntityNotFound_thenReturnFalse() {
        // arrange
        SecretValueDto dto = createSecretValueDto(1L, 1L);

        // act
        BooleanValueDto response = secretLengthValidatorService.validateValueLength(dto);

        // assert
        assertNotNull(response);
        assertFalse(response.getValue());
    }

    @Test
    void validateValueLength_whenKeystoreAliasEntityNotFound_thenReturnFalse() {
        // arrange
        SecretValueDto dto = createSecretValueDto(1L, 1L);
        when(keystoreRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        // act
        BooleanValueDto response = secretLengthValidatorService.validateValueLength(dto);

        // assert
        assertNotNull(response);
        assertFalse(response.getValue());
        verify(keystoreRepository).findById(1L);
        verify(keystoreAliasRepository).findById(1L);
    }

    @Test
    void validateValueLength_whenKeystoreFileNotFound_thenReturnFalse()
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        // arrange
        SecretValueDto dto = createSecretValueDto(1L, 1L);
        when(keystoreRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
        when(keystoreAliasRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
        when(keystoreDataService.getKeyStore(any(GetKeystore.class))).thenThrow(new FileNotFoundException());

        // act
        BooleanValueDto response = secretLengthValidatorService.validateValueLength(dto);

        // assert
        assertNotNull(response);
        assertFalse(response.getValue());
        verify(keystoreRepository).findById(1L);
        verify(keystoreAliasRepository).findById(1L);
        verify(keystoreDataService).getKeyStore(any(GetKeystore.class));
    }

    @Test
    void validateValueLength_whenEncryptionFailed_thenReturnFalse()
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // arrange
        SecretValueDto dto = createSecretValueDto(1L, 1L);
        when(keystoreRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
        when(keystoreAliasRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
        when(keystoreDataService.getKeyStore(any(GetKeystore.class))).thenReturn(mock(KeyStore.class));
        when(cryptoService.encrypt(any(), any())).thenThrow(new GmsException("Encryption failed", GMS_001));

        // act
        BooleanValueDto response = secretLengthValidatorService.validateValueLength(dto);

        // assert
        assertNotNull(response);
        assertFalse(response.getValue());
        verify(keystoreRepository).findById(1L);
        verify(keystoreAliasRepository).findById(1L);
        verify(keystoreDataService).getKeyStore(any(GetKeystore.class));
        verify(cryptoService).encrypt(any(), any());
    }

    @Test
    void validateValueLength_whenEncryptionSucceeded_thenReturnTrue()
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // arrange
        SecretValueDto dto = createSecretValueDto(1L, 1L);
        when(keystoreRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
        when(keystoreAliasRepository.findById(1L)).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
        when(keystoreDataService.getKeyStore(any(GetKeystore.class))).thenReturn(mock(KeyStore.class));
        when(cryptoService.encrypt(any(), any())).thenReturn("encryptedValue");

        // act
        BooleanValueDto response = secretLengthValidatorService.validateValueLength(dto);

        // assert
        assertNotNull(response);
        assertTrue(response.getValue());
        assertLogContains(logAppender, "Encrypted secret value size: 14");
        verify(keystoreRepository).findById(1L);
        verify(keystoreAliasRepository).findById(1L);
        verify(keystoreDataService).getKeyStore(any(GetKeystore.class));
        verify(cryptoService).encrypt(any(), any());
    }
}
