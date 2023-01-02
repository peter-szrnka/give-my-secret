package io.github.gms.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiIntegrationTest extends AbstractIntegrationTest {

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
	}

	@Test
	void testGetSecret() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getApiHttpHeaders(DemoDataProviderService.API_KEY_CREDENTIAL1, jwt));
		ResponseEntity<ApiResponseDto> response = executeHttpGet("/api/secret/" + DemoDataProviderService.SECRET_ID1, requestEntity, ApiResponseDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(DemoDataProviderService.ENCRYPTED_VALUE, response.getBody().getValue());
	}
}
