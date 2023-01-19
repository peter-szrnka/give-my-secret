package io.github.gms.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiIntegrationTest extends AbstractIntegrationTest {
	
	@Autowired
	private ApiKeyRepository apiKeyRepository;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
	}

	@Test
	void testGetSecret() {
		// arrange
		apiKeyRepository.save(TestUtils.createApiKey(DemoDataProviderService.API_KEY_3_ID, DemoDataProviderService.API_KEY_CREDENTIAL3));
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getApiHttpHeaders(DemoDataProviderService.API_KEY_CREDENTIAL3));
		ResponseEntity<ApiResponseDto> response = executeHttpGet("/api/secret/" + DemoDataProviderService.SECRET_ID1, requestEntity, ApiResponseDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(DemoDataProviderService.ENCRYPTED_VALUE, response.getBody().getValue());
		
		apiKeyRepository.deleteById(DemoDataProviderService.API_KEY_3_ID);
	}
}
