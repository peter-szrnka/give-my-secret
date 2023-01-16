package io.github.gms.common.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.common.util.Constants;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ActiveProfiles({"secure"})
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class AuthenticationSecureIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldNotAutenticate() {
		
		// act
		AuthenticateRequestDto dto = new AuthenticateRequestDto(DemoDataProviderService.USERNAME1, "testFail");
		HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(dto);
		HttpClientErrorException.Unauthorized exception = 
				assertThrows(HttpClientErrorException.Unauthorized.class, () -> executeHttpPost(LoginController.LOGIN_PATH, requestEntity, String.class));
		
		// assert
		assertEquals("401 : [no body]", exception.getMessage());
	}
	
	@Test
	void shouldAutenticate() {
		
		// act
		AuthenticateRequestDto dto = new AuthenticateRequestDto(DemoDataProviderService.USERNAME1, "test");
		HttpEntity<AuthenticateRequestDto> requestEntity = new HttpEntity<>(dto);
		ResponseEntity<String> response = executeHttpPost(LoginController.LOGIN_PATH, requestEntity, String.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		List<String> cookies = response.getHeaders().get("Set-Cookie");
		assertFalse(cookies.isEmpty());
		assertEquals(2, cookies.size());
		assertTrue(cookies.stream().anyMatch(cookie -> cookie.startsWith(Constants.ACCESS_JWT_TOKEN)));
		assertTrue(cookies.stream().anyMatch(cookie -> cookie.startsWith(Constants.REFRESH_JWT_TOKEN)));
		assertNotNull(response.getBody());
	}

	@Test
	void shouldLogout() {
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPost(LoginController.LOGOUT_PATH, requestEntity, String.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().keySet().stream().anyMatch(header -> header.equalsIgnoreCase(Constants.SET_COOKIE)));
	}
}