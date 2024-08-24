package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.SystemStatusDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.common.util.Constants.OK;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(SystemController.class)
class SystemStatusIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

	@Test
	@TestedMethod("status")
	void shouldSystemStatusOK() {
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<SystemStatusDto> response = executeHttpGet("/system/status", requestEntity, SystemStatusDto.class);
		
		// assert
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(OK, response.getBody().getStatus());
		assertNotNull(response.getBody().getVersion());
		assertEquals(SELECTED_AUTH_DB, response.getBody().getAuthMode());
	}

	@Test
	@TestedMethod("getErrorCodes")
	void shouldGetErrorCodes() {

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		ResponseEntity<String> response = executeHttpGet("/error_codes", requestEntity, String.class);

		// assert
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
