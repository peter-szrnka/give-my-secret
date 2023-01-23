package io.github.gms.client.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gms.client.GiveMySecretClient;
import io.github.gms.client.model.GetSecretRequest;
import io.github.gms.client.model.GetSecretResponse;
import io.github.gms.client.model.GiveMySecretClientConfig;
import io.github.gms.client.util.ConnectionUtils;

/**
 * Unit test of {@link GiveMySecretClientImpl}
 * @author Peter Szrnka
 */
@ExtendWith(MockitoExtension.class)
class GiveMySecretClientImplTest {
	
	private InputStream mockKeystorestream = mock(InputStream.class);

	@Test
	void shouldFailWhenNoConfigProvided() {
		// act
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> GiveMySecretClient.create(null));
		
		// arrange
		assertEquals("Configration is mandatory!", exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("inputData")
	void shouldRunSync(String apiKey, String secretId, boolean keystoreRequired, String keystoreCredential, String keystoreAlias, String keystoreAliasCredential, String expectedMessage,
			boolean mockConnection) {
		// arrange
		GiveMySecretClientConfig config = GiveMySecretClientConfig.builder()
				.withUrl("http://localhost:8080")
				.build();
		
		GetSecretRequest request = GetSecretRequest.builder()
				.withApiKey(apiKey)
				.withKeystore(keystoreRequired ? mockKeystorestream : null)
				.withSecretId(secretId)
				.withKeystoreAliasCredential(keystoreAliasCredential)
				.withKeystoreCredential(keystoreCredential)
				.withKeystoreAlias(keystoreAlias)
				.build();
		
		MockedStatic<ConnectionUtils> mockedConnectionUtils = mockStatic(ConnectionUtils.class);
		mockedConnectionUtils.when(() -> ConnectionUtils.getResponse(config, request)).thenReturn(GetSecretResponse.builder().withValue("test-value").build());
		
		// act
		GiveMySecretClient client = GiveMySecretClient.create(config);
		
		if (expectedMessage != null) {
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> client.getSecret(request));
			
			// arrange
			assertEquals(expectedMessage, exception.getMessage());
			
			mockedConnectionUtils.close();
		} else {
			try {
				GetSecretResponse response = client.getSecret(request);
				assertNotNull(response);
				assertEquals("test-value", response.getValue());
			} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			} finally {
				mockedConnectionUtils.close();
			}
		}
	}
	
	public static Object[][] inputData() {
		return new Object[][] {
			{ null, null, false, null, null, null, "API key is mandatory!", false },
			{ "apiKey", null, false, null, null, null, "Secret Id is mandatory!", false },
			{ "apiKey", "secretID", false, null, null, null, null, true },
			{ "apiKey", "secretID", true, null, null, null, "Invalid configuration: All keystore parameter must be set if keystore stream defined!", true },
			{ "apiKey", "secretID", true, "test", null, null, "Invalid configuration: All keystore parameter must be set if keystore stream defined!", true },
			{ "apiKey", "secretID", true, "test", "test", null, "Invalid configuration: All keystore parameter must be set if keystore stream defined!", true },
			{ "apiKey", "secretID", true, "test", "test", "test", null, true }
		};
	}
}
