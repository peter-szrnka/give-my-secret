package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
class HealthcheckIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldReturnHttp200() {

		// act
		ResponseEntity<Void> response = executeHttpGet("/healthcheck", new HttpEntity<>(null), Void.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
