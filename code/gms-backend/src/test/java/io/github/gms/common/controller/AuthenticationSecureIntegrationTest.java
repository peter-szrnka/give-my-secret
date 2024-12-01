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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.github.gms.common.util.Constants.*;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static io.github.gms.util.TestConstants.TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ActiveProfiles({"secure"})
@Tag(TAG_INTEGRATION_TEST)
class AuthenticationSecureIntegrationTest extends AbstractIntegrationTest {

	@Test
	void test_whenUserProvidedInvalidCredentials_thenReturnUnauthorized() {
		AuthenticateRequestDto dto = new AuthenticateRequestDto(DemoData.USERNAME1, "testFail");
		HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(dto);

		// act
		ResponseEntity<String> response = executeHttpPost(SLASH + LoginController.LOGIN_PATH, requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	@Test
	void test_whenLoginCalled_thenReturnOk() {
		// act
		AuthenticateRequestDto dto = new AuthenticateRequestDto(DemoData.USERNAME1, TEST);
		HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<String> response = executeHttpPost(SLASH + LoginController.LOGIN_PATH, requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());

		List<String> cookies = response.getHeaders().get("Set-Cookie");
		assertFalse(cookies.isEmpty());
		assertEquals(2, cookies.size());
		assertTrue(cookies.stream().anyMatch(cookie -> cookie.startsWith(ACCESS_JWT_TOKEN)));
		assertTrue(cookies.stream().anyMatch(cookie -> cookie.startsWith(REFRESH_JWT_TOKEN)));
		assertNotNull(response.getBody());
	}

	@Test
	void test_whenLogoutCalled_thenReturnOk() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost(SLASH + LoginController.LOGOUT_PATH, requestEntity, String.class);

		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().keySet().stream().anyMatch(header -> header.equalsIgnoreCase(SET_COOKIE)));
	}
}