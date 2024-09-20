package io.github.gms.auth;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthorizationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.LogAssertionUtils.assertLogEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class AuthorizationServiceImplTest extends AbstractLoggingUnitTest {

	private TokenGeneratorService tokenGeneratorService;
	private JwtService jwtService;
	private UserAuthService userAuthService;
	private SystemPropertyService systemPropertyService;
	private AuthorizationServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		// init
		tokenGeneratorService = mock(TokenGeneratorService.class);
		jwtService = mock(JwtService.class);
		userAuthService = mock(UserAuthService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		service = new AuthorizationServiceImpl(jwtService, tokenGeneratorService, systemPropertyService, userAuthService);

		addAppender(AuthorizationServiceImpl.class);
	}
	
	@Test
	void jwtTokenIsMissing() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] {});

		// act
		AuthorizationResponse response = service.authorize(req);
		
		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
		assertEquals("Access denied!", response.getErrorMessage());
	}

	@Test
	void jwtIsInvalid() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(ACCESS_JWT_TOKEN, "invalid_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenThrow(new RuntimeException("Wrong JWT token!"));
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");

		// act
		AuthorizationResponse response = service.authorize(req);
		
		// assert
		assertLogContains(logAppender, "Authorization failed: ");
		assertEquals("Authorization failed!", response.getErrorMessage());
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void jwtHasExpired() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(ACCESS_JWT_TOKEN, "expired_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(
				ZonedDateTime.now().minusDays(1L)
			      .toInstant()));
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");

		// act
		AuthorizationResponse response = service.authorize(req);
		
		// assert
		assertLogEquals(logAppender, "Authentication failed: JWT token has expired!");
		assertEquals("JWT token has expired!", response.getErrorMessage());
		assertEquals(HttpStatus.BAD_REQUEST, response.getResponseStatus());
		
		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void userIsBlocked() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		UserDetails userDetails = TestUtils.createBlockedGmsUser();

		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(ACCESS_JWT_TOKEN, "valid_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(ZonedDateTime.now().plusDays(1L).toInstant()));
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(claims.get(anyString(), any())).thenReturn(userDetails.getUsername());
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");

		// act
		AuthorizationResponse response = service.authorize(req);
		
		// assert
		assertLogEquals(logAppender, "User is blocked");
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
		assertEquals("User is blocked", response.getErrorMessage());

		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void jwtIsValid() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		UserDetails userDetails = TestUtils.createGmsUser();

		when(req.getRemoteAddr()).thenReturn("127.0.0.1");
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(ACCESS_JWT_TOKEN, "valid_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(ZonedDateTime.now().plusDays(1L).toInstant()));
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(claims.get(anyString(), any())).thenReturn(userDetails.getUsername());
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");

		when(tokenGeneratorService.getAuthenticationDetails(any(GmsUserDetails.class))).thenReturn(Map.of(
				JwtConfigType.ACCESS_JWT, "ACCESS_JWT",
				JwtConfigType.REFRESH_JWT, "REFRESH_JWT"
				));

		// act
		AuthorizationResponse response = service.authorize(req);
		
		// assert
		assertLogEquals(logAppender, "Authentication failed: JWT token has expired!");
		assertEquals(HttpStatus.OK, response.getResponseStatus());
		assertTrue(response.getJwtPair().toString().contains("REFRESH_JWT=REFRESH_JWT"));
		assertTrue(response.getJwtPair().toString().contains("ACCESS_JWT=ACCESS_JWT"));

		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) response.getAuthentication();
		assertNotNull(token.getDetails());
		assertNotNull(token.getAuthorities());
		assertTrue(token.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

		WebAuthenticationDetails tokenDetails = (WebAuthenticationDetails) token.getDetails();
		assertEquals("127.0.0.1", tokenDetails.getRemoteAddress());
		assertEquals("username1", token.getName());

		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);

		verify(tokenGeneratorService).getAuthenticationDetails(any(GmsUserDetails.class));
	}
}
