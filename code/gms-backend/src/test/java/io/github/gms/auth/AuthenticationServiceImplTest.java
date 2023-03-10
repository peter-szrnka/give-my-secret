package io.github.gms.auth;

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
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationDetails;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
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
	private SystemPropertyService systemPropertyService;
	
	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserAuthService userAuthService;
	
	@Mock
	private GenerateJwtRequestConverter generateJwtRequestConverter;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		((Logger) LoggerFactory.getLogger(AuthenticationServiceImpl.class)).addAppender(logAppender);
	}
	
	@Test
	void shouldAuthenticate() {
		// arrange
		when(authenticationManager.authenticate(any()))
			.thenReturn(new TestingAuthenticationToken(TestUtils.createGmsUser(), "cred", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of()).expirationDateInSeconds(900L).build());
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of()).expirationDateInSeconds(900L).build());
		when(jwtService.generateJwts(anyMap())).thenReturn(Map.of(
				JwtConfigType.ACCESS_JWT, "ACCESS_JWT",
				JwtConfigType.REFRESH_JWT, "REFRESH_JWT"
				));
		
		//act
		AuthenticationDetails response = service.authenticate("user", "credential");
		
		// assert
		assertNotNull(response);
		verify(authenticationManager).authenticate(any());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap());
		verify(jwtService).generateJwts(anyMap());
	}
	
	@Test
	void jwtTokenIsMissing() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] {});

		// act
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		//assertFalse(response.isSkip());
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
		assertEquals("Access denied!", response.getErrorMessage());
	}

	@Test
	void jwtIsInvalid() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.ACCESS_JWT_TOKEN, "invalid_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenThrow(new RuntimeException("Wrong JWT token!"));
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");

		// act
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().contains("Authentication failed: ")));
		//assertFalse(response.isSkip());
		assertEquals("Authentication failed!", response.getErrorMessage());
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());
		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void jwtHasExpired() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		
		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.ACCESS_JWT_TOKEN, "expired_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(
				ZonedDateTime.now().minusDays(1l)
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

		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.ACCESS_JWT_TOKEN, "valid_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(ZonedDateTime.now().plusDays(1l).toInstant()));
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(claims.get(anyString(), any())).thenReturn(userDetails.getUsername());
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");

		// act
		AuthenticationResponse response = service.authorize(req);
		
		// assert
		assertTrue(logAppender.list.stream().anyMatch(log -> log.getFormattedMessage().equals("User is blocked")));
		assertEquals(HttpStatus.FORBIDDEN, response.getResponseStatus());

		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void jwtIsValid() {
		// arrange
		HttpServletRequest req = mock(HttpServletRequest.class);
		Claims claims = mock(Claims.class);
		UserDetails userDetails = TestUtils.createGmsUser();

		when(req.getCookies()).thenReturn(new Cookie[] { new Cookie(Constants.ACCESS_JWT_TOKEN, "valid_token")});
		when(jwtService.parseJwt(anyString(), anyString())).thenReturn(claims);
		when(claims.getExpiration()).thenReturn(Date.from(ZonedDateTime.now().plusDays(1l).toInstant()));
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(claims.get(anyString(), any())).thenReturn(userDetails.getUsername());
		when(systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM)).thenReturn("HS512");
		
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of()).expirationDateInSeconds(900L).build());
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
		verify(jwtService).parseJwt(anyString(), anyString());
		verify(systemPropertyService).get(SystemProperty.ACCESS_JWT_ALGORITHM);
		
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap());
		verify(jwtService).generateJwts(anyMap());
	}
}
