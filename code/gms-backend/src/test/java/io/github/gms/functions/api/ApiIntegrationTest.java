package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.keystore.KeystoreAliasEntity;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(ApiController.class)
class ApiIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

	@Autowired
	private KeystoreAliasRepository keystoreAliasRepository;

    @Test
	@TestedMethod("getSecret")
	void getSecret_whenInputIsValid_thenReturnData() {
		// arrange
        ApiKeyEntity apiKeyEntity = apiKeyRepository.save(TestUtils.createApiKey(null, DemoData.API_KEY_CREDENTIAL3));
        KeystoreAliasEntity keystoreAliasEntity =  keystoreAliasRepository.save(TestUtils.createKeystoreAliasEntity(null, DemoData.KEYSTORE_ID));
		SecretEntity secretEntity = secretRepository.save(
				TestUtils.createSecretEntity(null, keystoreAliasEntity.getId(), DemoData.SECRET_ID3));
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getApiHttpHeaders(DemoData.API_KEY_CREDENTIAL3));
		ResponseEntity<Map> response = executeHttpGet("/api/secret/" + secretEntity.getSecretId(), requestEntity, Map.class);

		secretRepository.deleteById(secretEntity.getId());
		apiKeyRepository.deleteById(apiKeyEntity.getId());
		keystoreAliasRepository.deleteById(keystoreAliasEntity.getId());

		// Assert
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(DemoData.ENCRYPTED_VALUE, response.getBody().get("value"));
	}

	@Test
	void getSecret_whenHeaderIsMissing_thenReturnBadRequest() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getApiHttpHeaders(null));
		ResponseEntity<String> response = executeHttpGet("/api/secret/" + DemoData.SECRET_ID3, requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void getSecret_whenApiKeyIsInvalid_thenReturnInternalServerError() {
		// arrange
        ApiKeyEntity newEntity = apiKeyRepository.save(TestUtils.createApiKey(null, DemoData.API_KEY_CREDENTIAL3));

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getApiHttpHeaders(DemoData.API_KEY_CREDENTIAL3));
		ResponseEntity<String> response = executeHttpGet("/api/secret/fake-key", requestEntity, String.class);

		apiKeyRepository.deleteById(newEntity.getId());
		// Assert
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}
}