package io.github.gms.api.service.impl;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.api.service.KeystoreValidatorService;
import io.github.gms.api.service.SecretPreparationService;
import io.github.gms.common.enums.SecretType;
import io.github.gms.secure.dto.GetSecretRequestDto;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.service.CryptoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.github.gms.common.util.Constants.VALUE;
import static io.github.gms.util.TestUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createMockSecret;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link ApiServiceImpl}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiServiceImplTest extends AbstractUnitTest {

	private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");
	private ListAppender<ILoggingEvent> logAppender;
	private CryptoService cryptoService;
	private SecretPreparationService secretPreparationService;
	private KeystoreValidatorService keystoreValidatorService;
	private ApiServiceImpl service;

	@BeforeEach
	void beforeEach() {
		cryptoService = mock(CryptoService.class);
		secretPreparationService = mock(SecretPreparationService.class);
		keystoreValidatorService = mock(KeystoreValidatorService.class);
		service = new ApiServiceImpl(cryptoService, secretPreparationService, keystoreValidatorService);

		logAppender = new ListAppender<>();
		logAppender.start();
		((Logger) LoggerFactory.getLogger(ApiServiceImpl.class)).addAppender(logAppender);
	}

	@AfterEach
	void tearDown() {
		logAppender.list.clear();
		logAppender.stop();
	}

	@ParameterizedTest
	@MethodSource("inputData")
	void shouldReturnValue(boolean returnDecrypted, SecretType type, String expectedValue) {
		// arrange
		when(secretPreparationService.getSecretEntity(dto)).thenReturn(createMockSecret(expectedValue, returnDecrypted, type));

		if (returnDecrypted) {
			when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn(expectedValue);
		}

		// act
		Map<String, String> response = service.getSecret(dto);

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

		assertLogContains(logAppender, "Searching for secret=");
		verify(secretPreparationService).getSecretEntity(dto);
		verify(keystoreValidatorService).validateSecretKeystore(any(SecretEntity.class));
		verify(cryptoService, returnDecrypted ? times(1) : never()).decrypt(any(SecretEntity.class));
	}

	public static Object[][] inputData() {
		return new Object[][] {
				{ true, SecretType.SIMPLE_CREDENTIAL, "decrypted" },
				{ false, SecretType.SIMPLE_CREDENTIAL, "encrypted" },
				{ true, SecretType.MULTIPLE_CREDENTIAL, "username:u;password:p" },
				{ false, SecretType.MULTIPLE_CREDENTIAL, "encrypted" } };
	}
}
