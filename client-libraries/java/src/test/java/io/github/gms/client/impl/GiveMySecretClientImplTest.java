package io.github.gms.client.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gms.client.GiveMySecretClient;
import io.github.gms.client.enums.KeystoreType;
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
	
	private static final String TEST_DECRYPTED_VALUE = "my-value";
	private static final String TEST_ENCRYPTED_VALUE = "fTFllqRl39VoaYwCdpDNP1CAHWdhFCLUSJf+OOJyRzc05x1PslQihY4NM67LTAocOga1iePFNps0F4VL//kuXQV+rpLPowu7rvoVZl90Gau3fnF2ck7C2CY1ScBW0nIFuuEe+eya1eAFbMYQrYFx2NyaWug6ARfJxOxgcNYAW3av3rMkKw2CsjgAg7OHrg2f6d4TzxYoUVrcmMu+dziKf+vWAkKRuS+rg4NKmCkFg8hj1haa7Or8SNr+iBgx2TBAOEpPhidS6W/Mu5kmS9q5tI8+TPF2MjnHsGGRvKzQZilFSwk3C34BmiDiqtvYeDTfYaQsavGXd9ggarR2sQhkMA";
	
	private InputStream keystoreStream;
	
	@BeforeEach
	private void setup() {
		keystoreStream = getClass().getClassLoader().getResourceAsStream("test.jks");
	}

	@Test
	void shouldFailWhenNoConfigProvided() {
		// act
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> GiveMySecretClient.create(null));
		
		// arrange
		assertEquals("Configration is mandatory!", exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("inputData")
	void shouldRunSync(InputData input) {
		// arrange
		GiveMySecretClientConfig config = GiveMySecretClientConfig.builder()
				.withUrl("http://localhost:8080")
				.build();
		
		GetSecretRequest request = GetSecretRequest.builder()
				.withApiKey(input.getApiKey())
				.withKeystoreType(input.getType())
				.withKeystore(input.isKeystoreRequired() ? keystoreStream : null)
				.withSecretId(input.getSecretId())
				.withKeystoreAliasCredential(input.getKeystoreAliasCredential())
				.withKeystoreCredential(input.getKeystoreCredential())
				.withKeystoreAlias(input.getKeystoreAlias())
				.build();
		
		MockedStatic<ConnectionUtils> mockedConnectionUtils = mockStatic(ConnectionUtils.class);
		mockedConnectionUtils.when(() -> ConnectionUtils.getResponse(config, request)).thenReturn(GetSecretResponse.builder().withValue(input.getValue()).build());
		
		// act
		GiveMySecretClient client = GiveMySecretClient.create(config);
		
		if (input.getExpectedMessage() != null) {
			Exception exception = assertThrows(Exception.class, () -> client.getSecret(request));
			
			// arrange
			assertEquals(input.getExpectedMessage(), exception.getMessage());
			
			mockedConnectionUtils.close();
		} else {
			try {
				GetSecretResponse response = client.getSecret(request);
				assertNotNull(response);
				assertEquals("my-value", response.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mockedConnectionUtils.close();
			}
		}
	}
	
	public static InputData[] inputData() {
		return new InputData[] {
			// API key is missing
			InputData.builder().withExpectedMessage("API key is mandatory!").build(),
			// SecretId is missing
			InputData.builder().withApiKey("apiKey").withExpectedMessage("Secret Id is mandatory!").build(),
			// Successful execution with decrypted data
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withValue(TEST_DECRYPTED_VALUE).build(),
			// Keystore credential missing
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withKeystoreRequired(true)
				.withExpectedMessage("Invalid configuration: All keystore parameter must be set if keystore stream defined!").build(),
			// Keystore credential missing
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withKeystoreRequired(true).withType(KeystoreType.JKS)
				.withExpectedMessage("Invalid configuration: All keystore parameter must be set if keystore stream defined!").build(),
			// Keystore alias missing
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withKeystoreRequired(true).withType(KeystoreType.JKS).withKeystoreCredential("test")
				.withExpectedMessage("Invalid configuration: All keystore parameter must be set if keystore stream defined!").build(),
			// Keystore alias credential missing
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withKeystoreRequired(true).withType(KeystoreType.JKS)
				.withKeystoreCredential("test").withKeystoreAlias("test")
				.withExpectedMessage("Invalid configuration: All keystore parameter must be set if keystore stream defined!").build(),
			// Failed execution with encrypted data
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withKeystoreRequired(true).withType(KeystoreType.JKS)
				.withKeystoreAlias("test").withKeystoreCredential("test").withKeystoreAliasCredential("test").withValue("invalid").withExpectedMessage("Message cannot be decrypted!").build(),
			// Successful execution with encrypted data
			InputData.builder().withApiKey("apiKey").withSecretId("secretId").withKeystoreRequired(true).withType(KeystoreType.JKS)
				.withKeystoreAlias("test").withKeystoreCredential("test").withKeystoreAliasCredential("test").withValue(TEST_ENCRYPTED_VALUE).build(),
		};
	}
}
