package io.github.gms.functions.api;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.service.CryptoService;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.github.gms.common.util.Constants.VALUE;
import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class SecretValueProviderServiceImplTest extends AbstractLoggingUnitTest {

    private KeystoreValidatorService keystoreValidatorService;
    private CryptoService cryptoService;
    private SecretValueProviderServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        keystoreValidatorService = mock(KeystoreValidatorService.class);
        cryptoService = mock(CryptoService.class);
        service = new SecretValueProviderServiceImpl(keystoreValidatorService, cryptoService);

        ((Logger) LoggerFactory.getLogger(SecretValueProviderServiceImpl.class)).addAppender(logAppender);
    }

    @Test
    void shouldReturnEncrypted() {
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
}
