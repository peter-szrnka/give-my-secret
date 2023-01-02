package io.github.gms.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.service.JwtService;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;

/**
 * Unit test of {@link AuthenticationServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class AuthenticationServiceImplTest extends AbstractLoggingUnitTest {

	@InjectMocks
	private AuthenticationServiceImpl service;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserAuthService userAuthService;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		((Logger) LoggerFactory.getLogger(AuthenticationServiceImpl.class)).addAppender(logAppender);
	}
	
	/*@Test
	void shouldSkipByUrl() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getRequestURI()).thenReturn("/healthcheck");

		// act
		AuthenticationResponse response = service.authenticate(req);
		
		// assert
		assertTrue(response.isSkip());
	}*/
	
	@Test
	void jwtTokenIsMissing() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] {});

		// act
		AuthenticationResponse response = service.authenticate(req);
		
		// assert
		//assertFalse(response.isSkip());
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
		assertEquals("Access denied!", response.getErrorMessage());
	}

	@Test
	void jwtIsInvalid() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.JWT_TOKEN, "invalid_token")});
		when(jwtService.parseJwt(anyString())).thenThrow(new RuntimeException("Wrong JWT token!"));

		// act
		AuthenticationResponse response = service.authenticate(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().contains("Authentication failed: ")));
		//assertFalse(response.isSkip());
		assertEquals("Authentication failed!", response.getErrorMessage());
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
	}
	
	@Test
	void jwtHasExpired() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.JWT_TOKEN, "expired_token")});
		when(jwtService.parseJwt(anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(
				LocalDateTime.now().minusDays(1l).atZone(ZoneId.systemDefault())
			      .toInstant()));

		// act
		AuthenticationResponse response = service.authenticate(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("Authentication failed: JWT token has expired!")));
		//assertFalse(response.isSkip());
		assertEquals("JWT token has expired!", response.getErrorMessage());
		assertEquals(HttpStatus.BAD_REQUEST, response.getResponseStatus());
	}
	
	@Test
	void userIsBlocked() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		UserDetails userDetails = TestUtils.createBlockedGmsUser();

		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.JWT_TOKEN, "valid_token")});
		when(jwtService.parseJwt(anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(
				LocalDateTime.now().plusDays(1l).atZone(ZoneId.systemDefault())
			      .toInstant()));
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(claims.get(anyString(), any())).thenReturn(userDetails.getUsername());

		// act
		AuthenticationResponse response = service.authenticate(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("User is blocked")));
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
	}
	
	@Test
	void jwtIsValid() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		UserDetails userDetails = TestUtils.createGmsUser();

		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.JWT_TOKEN, "valid_token")});
		when(jwtService.parseJwt(anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(
				LocalDateTime.now().plusDays(1l).atZone(ZoneId.systemDefault())
			      .toInstant()));
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(claims.get(anyString(), any())).thenReturn(userDetails.getUsername());

		// act
		AuthenticationResponse response = service.authenticate(req);
		
		// assert
		assertFalse(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("Authentication failed: JWT token has expired!")));
		//assertFalse(response.isSkip());
		assertEquals(HttpStatus.OK, response.getResponseStatus());
	}
}
