package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
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
@TestedClass(HealthcheckController.class)
class HealthcheckIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {

	@Test
	@TestedMethod("healthcheck")
	void healthcheck_whenCalled_thenReturnOk() {

		// act
		ResponseEntity<Void> response = executeHttpGet("/healthcheck", new HttpEntity<>(null), Void.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
