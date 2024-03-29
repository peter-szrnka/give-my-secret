package io.github.gms.auth;

import ch.qos.logback.classic.Logger;
import dev.samstevens.totp.code.CodeVerifier;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.converter.GenerateJwtRequestConverter;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	private CodeVerifier verifier;
	private UserService userService;
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
		verifier = mock(CodeVerifier.class);
		userService = mock(UserService.class);
		service = new AuthenticationServiceImpl(authenticationManager, jwtService,
				systemPropertyService, generateJwtRequestConverter, userConverter, userAuthService, verifier, userService);

		((Logger) LoggerFactory.getLogger(AuthenticationServiceImpl.class)).addAppender(logAppender);
	}

	@Test
	void shouldAuthenticateFailWhenUserIsBlocked() {
		// arrange
		when(userService.isBlocked("user")).thenReturn(true);

		// act
		AuthenticationResponse response = service.authenticate("user", "credential");

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userService).isBlocked("user");
	}

	@Test
	void shouldAuthenticateFailWhenUserIsLockedInLdap() {
		// arrange
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setAccountNonLocked(false);
		when(userService.isBlocked("user")).thenReturn(false);
		when(authenticationManager.authenticate(any()))
				.thenReturn(new TestingAuthenticationToken(userDetails, "cred", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

		// act
		AuthenticationResponse response = service.authenticate("user", "credential");

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userService).isBlocked("user");
	}

	@Test
	void shouldAuthenticateFail() {
		// arrange
		when(userService.isBlocked("user")).thenReturn(false);
		when(authenticationManager.authenticate(any())).thenThrow(IllegalArgumentException.class);

		// act
		AuthenticationResponse response = service.authenticate("user", "credential");

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().equalsIgnoreCase("Login failed")));
		verify(userService).isBlocked("user");
	}
	
	@ParameterizedTest
	@MethodSource("nonMfaTestData")
	void shouldAuthenticate(boolean systemLevelMfaEnabled, boolean userMfaEnabled) {
		// arrange
		when(userService.isBlocked("user")).thenReturn(false);
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setMfaEnabled(userMfaEnabled);
		when(authenticationManager.authenticate(any()))
			.thenReturn(new TestingAuthenticationToken(userDetails, "cred", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of("role","ROLE_USER")).expirationDateInSeconds(900L).build());
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of("role","ROLE_USER")).expirationDateInSeconds(900L).build());
		when(jwtService.generateJwts(anyMap())).thenReturn(Map.of(
				JwtConfigType.ACCESS_JWT, "ACCESS_JWT",
				JwtConfigType.REFRESH_JWT, "REFRESH_JWT"
				));
		when(userConverter.toUserInfoDto(any(GmsUserDetails.class), eq(false))).thenReturn(TestUtils.createUserInfoDto());
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA)).thenReturn(false);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MFA)).thenReturn(systemLevelMfaEnabled);
		
		//act
		AuthenticationResponse response = service.authenticate("user", "credential");
		
		// assert
		assertNotNull(response);
		assertEquals("ACCESS_JWT", response.getToken());
		assertEquals("REFRESH_JWT", response.getRefreshToken());
		assertEquals(AuthResponsePhase.COMPLETED, response.getPhase());

		verify(userService).isBlocked("user");
		verify(userService).resetLoginAttempt("user");
		verify(authenticationManager).authenticate(any());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap());
		ArgumentCaptor<Map<JwtConfigType, GenerateJwtRequest>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
		verify(jwtService).generateJwts(argumentCaptor.capture());
		Map<JwtConfigType, GenerateJwtRequest> capturedMap = argumentCaptor.getValue();
		assertNotNull(capturedMap.get(JwtConfigType.ACCESS_JWT));
		assertNotNull(capturedMap.get(JwtConfigType.REFRESH_JWT));
		assertEquals(1, capturedMap.get(JwtConfigType.ACCESS_JWT).getClaims().size());
		assertEquals(1, capturedMap.get(JwtConfigType.REFRESH_JWT).getClaims().size());

		verify(userConverter).toUserInfoDto(any(GmsUserDetails.class), eq(false));
		verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);
		verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_MFA);
	}

	@ParameterizedTest
	@MethodSource("mfaTestData")
	void shouldExpectMfa(boolean enableGlobalMfa, boolean enableMfa) {
		// arrange
		when(userService.isBlocked("user")).thenReturn(false);
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setMfaEnabled(true);
		when(authenticationManager.authenticate(any()))
			.thenReturn(new TestingAuthenticationToken(userDetails, "cred", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA)).thenReturn(enableGlobalMfa);
		if (!enableGlobalMfa) {
			when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MFA)).thenReturn(enableMfa);
		}
		when(userConverter.toUserInfoDto(any(GmsUserDetails.class), eq(true))).thenReturn(TestUtils.createUserInfoDto());
		
		//act
		AuthenticationResponse response = service.authenticate("user", "credential");
		
		// assert
		assertNotNull(response);
		assertNull(response.getToken());
		assertNull(response.getRefreshToken());
		assertEquals(AuthResponsePhase.MFA_REQUIRED, response.getPhase());

		verify(userService).isBlocked("user");
		verify(authenticationManager).authenticate(any());
		verify(userConverter).toUserInfoDto(any(GmsUserDetails.class), eq(true));
		verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);
		verify(systemPropertyService, enableGlobalMfa ? never() : times(1)).getBoolean(SystemProperty.ENABLE_MFA);
	}

	@Test
	void shouldVerifyThrowsAnError() {
		// arrange
		when(userService.isBlocked("user1")).thenReturn(false);
		LoginVerificationRequestDto dto = TestUtils.createLoginVerificationRequestDto();

		// act
		AuthenticationResponse response = service.verify(dto);

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		verify(userService).isBlocked("user1");
	}

	@Test
	void shouldVerifyFailWhenUserIsBlocked() {
		// arrange
		when(userService.isBlocked("user1")).thenReturn(true);

		// act
		AuthenticationResponse response = service.verify(TestUtils.createLoginVerificationRequestDto());

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userService).isBlocked("user1");
	}

	@Test
	void shouldVerifyFailWhenUserIsLockedInLdap() {
		// arrange
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setAccountNonLocked(false);
		when(userService.isBlocked("user1")).thenReturn(false);
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);

		// act
		AuthenticationResponse response = service.verify(TestUtils.createLoginVerificationRequestDto());

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userService).isBlocked("user1");
	}

	@Test
	void shouldVerifyFail() {
		// arrange
		when(userService.isBlocked("user1")).thenReturn(false);
		LoginVerificationRequestDto dto = TestUtils.createLoginVerificationRequestDto();
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(verifier.isValidCode(anyString(), anyString())).thenReturn(false);

		// act
		AuthenticationResponse response = service.verify(dto);

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		verify(userService).isBlocked("user1");
		verify(userService).updateLoginAttempt("user1");
	}

	@Test
	void shouldVerify() {
		// arrange
		when(userService.isBlocked("user1")).thenReturn(false);
		LoginVerificationRequestDto dto = TestUtils.createLoginVerificationRequestDto();
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(verifier.isValidCode(anyString(), anyString())).thenReturn(true);
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of()).expirationDateInSeconds(900L).build());
		when(generateJwtRequestConverter.toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap()))
			.thenReturn(GenerateJwtRequest.builder().algorithm("HS512").claims(Map.of()).expirationDateInSeconds(900L).build());
		when(jwtService.generateJwts(anyMap())).thenReturn(Map.of(
				JwtConfigType.ACCESS_JWT, "ACCESS_JWT",
				JwtConfigType.REFRESH_JWT, "REFRESH_JWT"
				));
		when(userConverter.toUserInfoDto(any(GmsUserDetails.class), eq(false))).thenReturn(TestUtils.createUserInfoDto());

		// act
		AuthenticationResponse response = service.verify(dto);

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.COMPLETED, response.getPhase());
		assertEquals("ACCESS_JWT", response.getToken());
		assertEquals("REFRESH_JWT", response.getRefreshToken());

		verify(userService).isBlocked("user1");
		verify(userAuthService).loadUserByUsername(anyString());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.ACCESS_JWT), anyString(), anyMap());
		verify(generateJwtRequestConverter).toRequest(eq(JwtConfigType.REFRESH_JWT), anyString(), anyMap());
		verify(jwtService).generateJwts(anyMap());
		verify(userConverter).toUserInfoDto(any(GmsUserDetails.class), eq(false));
	}

	private static Object[][] nonMfaTestData() {
		return new Object[][] {
			{ true, false },
			{ false, true },
			{ false, false }
		};
	}

	private static Object[][] mfaTestData() {
		return new Object[][] {
			{ true, false },
			{ true, true },
			{ false, true }
		};
	}
}
