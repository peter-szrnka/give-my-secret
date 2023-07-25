package io.github.gms.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class AuthenticationServiceImplTest extends AbstractLoggingUnitTest {

	private AuthenticationManager authenticationManager;
	private JwtService jwtService;
	private SystemPropertyService systemPropertyService;
	private GenerateJwtRequestConverter generateJwtRequestConverter;
	private UserConverter userConverter;
	private UserAuthService userAuthService;
	private AuthenticationServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		// init
		authenticationManager = mock(AuthenticationManager.class);
		jwtService = mock(JwtService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		generateJwtRequestConverter = mock(GenerateJwtRequestConverter.class);
		userConverter = mock(UserConverter.class);
		userAuthService = mock(UserAuthService.class);
		service = new AuthenticationServiceImpl(authenticationManager, jwtService,
				systemPropertyService, generateJwtRequestConverter, userConverter, userAuthService);

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
		AuthenticationResponse response = service.authenticate("user", "credential");
		
		// assert
		assertNotNull(response);
		verify(authenticationManager).authenticate(any());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap());
		verify(jwtService).generateJwts(anyMap());
	}
}
