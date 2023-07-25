package io.github.gms.auth;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class AuthorizationServiceImplTest extends AbstractLoggingUnitTest {

	private AuthenticationManager authenticationManager;
	private JwtService jwtService;
	private UserAuthService userAuthService;
	private SystemPropertyService systemPropertyService;
	private GenerateJwtRequestConverter generateJwtRequestConverter;
	private AuthorizationServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		// init
		authenticationManager = mock(AuthenticationManager.class);
		jwtService = mock(JwtService.class);
		userAuthService = mock(UserAuthService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		generateJwtRequestConverter = mock(GenerateJwtRequestConverter.class);
		service = new AuthorizationServiceImpl(authenticationManager, jwtService,
				systemPropertyService, generateJwtRequestConverter, userAuthService);

		((Logger) LoggerFactory.getLogger(AuthorizationServiceImpl.class)).addAppender(logAppender);
	}
	
	@Test
	void jwtTokenIsMissing() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] {});

		// act
		AuthenticationResponse response = service.authorize(req);
		
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
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().contains("Authorization failed: ")));
		//assertFalse(response.isSkip());
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
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("Authentication failed: JWT token has expired!")));
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
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("User is blocked")));
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
		
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of("k1", "v1", "k2", "v2", "k3", "v3")).expirationDateInSeconds(900L).build());
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of()).expirationDateInSeconds(900L).build());
		
		when(jwtService.generateJwts(anyMap())).thenReturn(Map.of(
				JwtConfigType.ACCESS_JWT, "ACCESS_JWT",
				JwtConfigType.REFRESH_JWT, "REFRESH_JWT"
				));

		// act
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		assertFalse(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("Authentication failed: JWT token has expired!")));
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
		
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap());

		ArgumentCaptor<Map<JwtConfigType, GenerateJwtRequest>> mapCaptor = ArgumentCaptor.forClass(Map.class);
		verify(jwtService).generateJwts(mapCaptor.capture());

		Map<JwtConfigType, GenerateJwtRequest> capturedMap = mapCaptor.getValue();
		assertEquals(3, capturedMap.get(JwtConfigType.ACCESS_JWT).getClaims().size());
	}
}
