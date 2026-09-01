package io.github.gms.common.controller;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.common.util.Constants.SET_COOKIE;
import static io.github.gms.common.util.Constants.SLASH;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
class AuthenticationUnsecureIntegrationTest extends AbstractIntegrationTest {

	@Test
	void test_whenUserProvidedInvalidCredentials_thenReturnUnauthorized() {
		AuthenticateRequestDto dto = new AuthenticateRequestDto(DemoData.USERNAME1, "testFail");
		HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(dto);

		// when
		ResponseEntity<String> response = executeHttpPost(SLASH + LoginController.LOGIN_PATH, requestEntity, String.class);

		// then
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	void test_whenLoginCalled_thenReturnOk() {
		// when
		AuthenticateRequestDto dto = new AuthenticateRequestDto(DemoData.USERNAME1, "test");
		HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<String> response = executeHttpPost(SLASH + LoginController.LOGIN_PATH, requestEntity, String.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	void test_whenLogoutCalled_thenReturnOk() {
		// when
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost(SLASH + LoginController.LOGOUT_PATH, requestEntity, String.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().toSingleValueMap().keySet().stream().anyMatch(header -> header.equalsIgnoreCase(SET_COOKIE)));
	}
}
