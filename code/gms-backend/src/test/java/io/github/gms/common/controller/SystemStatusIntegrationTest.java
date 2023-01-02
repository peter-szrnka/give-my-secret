package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.util.Constants;
import io.github.gms.util.TestConstants;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class SystemStatusIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldSystemStatusOK() {
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<SystemStatusDto> response = executeHttpGet("/system/status", requestEntity, SystemStatusDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(Constants.OK, response.getBody().getStatus());
		assertEquals(Constants.SELECTED_AUTH_DB, response.getBody().getAuthMode());
	}
}
