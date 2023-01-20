package io.github.gms.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiIntegrationTest extends AbstractIntegrationTest {
	
	@Autowired
	private ApiKeyRepository apiKeyRepository;
	
	@Autowired
	private KeystoreRepository keystoreRepository;

	@Autowired
	private KeystoreAliasRepository keystoreAliasRepository;

	@Autowired
	private SecretRepository secretRepository;
	
	@AfterEach
	void cleanup() {
		secretRepository.deleteById(DemoDataProviderService.SECRET_ENTITY3_ID);
		apiKeyRepository.deleteById(DemoDataProviderService.API_KEY_3_ID);
		keystoreAliasRepository.deleteById(DemoDataProviderService.KEYSTORE_ALIAS3_ID);
		keystoreRepository.deleteById(DemoDataProviderService.KEYSTORE3_ID);
	}

	@Test
	void testGetSecret() {
		// arrange
		apiKeyRepository.save(TestUtils.createApiKey(DemoDataProviderService.API_KEY_3_ID, DemoDataProviderService.API_KEY_CREDENTIAL3));
		keystoreAliasRepository.save(TestUtils.createKeystoreAliasEntity(DemoDataProviderService.KEYSTORE_ALIAS3_ID));
		keystoreRepository.save(TestUtils.createKeystoreEntity(DemoDataProviderService.KEYSTORE3_ID));
		secretRepository.save(
				TestUtils.createSecretEntity(DemoDataProviderService.SECRET_ENTITY3_ID, DemoDataProviderService.KEYSTORE_ALIAS3_ID, DemoDataProviderService.SECRET_ID3));
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getApiHttpHeaders(DemoDataProviderService.API_KEY_CREDENTIAL3));
		ResponseEntity<ApiResponseDto> response = executeHttpGet("/api/secret/" + DemoDataProviderService.SECRET_ID3, requestEntity, ApiResponseDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(DemoDataProviderService.ENCRYPTED_VALUE, response.getBody().getValue());
	}
}
