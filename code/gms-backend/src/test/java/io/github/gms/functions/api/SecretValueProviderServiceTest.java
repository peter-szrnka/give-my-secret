package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.service.CryptoService;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;

import static io.github.gms.common.util.Constants.VALUE;
import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretValueProviderServiceTest extends AbstractLoggingUnitTest {

    private KeystoreValidatorService keystoreValidatorService;
    private CryptoService cryptoService;
    private SecretValueProviderService service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        keystoreValidatorService = mock(KeystoreValidatorService.class);
        cryptoService = mock(CryptoService.class);
        service = new SecretValueProviderService(keystoreValidatorService, cryptoService);

        addAppender(SecretValueProviderService.class);
    }

    @Test
    void getSecretValue_whenDataIsValid_thenReturnEncrypted() {
        // arrange
        SecretEntity mockSecret = TestUtils.createSecretEntity();
        mockSecret.setValue("encrypted");
        mockSecret.setType(SecretType.SIMPLE_CREDENTIAL);

        // act
        Map<String, String> response = service.getSecretValue(mockSecret);

        // assert
        assertNotNull(response);
        assertEquals("encrypted", response.get(VALUE));
        assertEquals(SecretType.SIMPLE_CREDENTIAL.name(), response.get("type"));
        assertLogContains(logAppender, "Retrieve secretValue from entity=1");
        verify(keystoreValidatorService).validateSecretKeystore(mockSecret);
        verify(cryptoService, never()).decrypt(mockSecret);
    }

    @ParameterizedTest
    @MethodSource("inputData")
    void getSecretValue_whenDataIsValid_thenReturnEncrypted(boolean returnDecrypted, SecretType type, String expectedValue) {
        // arrange
        SecretEntity mockSecret = TestUtils.createSecretEntity();
        mockSecret.setValue("encrypted");
        mockSecret.setType(type);
        mockSecret.setReturnDecrypted(returnDecrypted);

        if (returnDecrypted) {
            when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn(expectedValue);

        }

        // act
        Map<String, String> response = service.getSecretValue(mockSecret);

        // assert
        assertNotNull(response);

        if (type == SecretType.SIMPLE_CREDENTIAL) {
            assertEquals(expectedValue, response.get(VALUE));
        } else if (returnDecrypted) {
            assertEquals("u", response.get("username"));
            assertEquals("p", response.get("password"));
        } else {
            assertEquals("encrypted", response.get(VALUE));
        }

        assertLogContains(logAppender, "Retrieve secretValue from entity=1");
        verify(keystoreValidatorService).validateSecretKeystore(any(SecretEntity.class));
        verify(cryptoService, returnDecrypted ? times(1) : never()).decrypt(any(SecretEntity.class));
    }

    private static Object[][] inputData() {
        return new Object[][] {
                { true, SecretType.SIMPLE_CREDENTIAL, "decrypted" },
                { false, SecretType.SIMPLE_CREDENTIAL, "encrypted" },
                { true, SecretType.MULTIPLE_CREDENTIAL, "username:u;password:p" },
                { false, SecretType.MULTIPLE_CREDENTIAL, "encrypted" } };
    }
}
