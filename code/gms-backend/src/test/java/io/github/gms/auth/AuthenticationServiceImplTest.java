package io.github.gms.auth;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserLoginAttemptManagerService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static io.github.gms.util.LogAssertionUtils.assertLogEqualsIgnoreCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class AuthenticationServiceImplTest extends AbstractLoggingUnitTest {

	private TokenGeneratorService tokenGeneratorService;
	private AuthenticationManager authenticationManager;
	private SystemPropertyService systemPropertyService;
	private UserConverter userConverter;
	private UserLoginAttemptManagerService userLoginAttemptManagerService;
	private AuthenticationServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		// init
		tokenGeneratorService = mock(TokenGeneratorService.class);
		authenticationManager = mock(AuthenticationManager.class);
		systemPropertyService = mock(SystemPropertyService.class);
		userConverter = mock(UserConverter.class);
		userLoginAttemptManagerService = mock(UserLoginAttemptManagerService.class);
		service = new AuthenticationServiceImpl(tokenGeneratorService,
				systemPropertyService, authenticationManager, userConverter, userLoginAttemptManagerService);

		addAppender(AuthenticationServiceImpl.class);
	}

	@Test
	void shouldAuthenticateFailWhenUserIsBlocked() {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user")).thenReturn(true);

		// act
		AuthenticationResponse response = service.authenticate("user", "credential");

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user");
	}

	@Test
	void shouldAuthenticateFailWhenUserIsLockedInLdap() {
		// arrange
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setAccountNonLocked(false);
		when(userLoginAttemptManagerService.isBlocked("user")).thenReturn(false);
		when(authenticationManager.authenticate(any()))
				.thenReturn(new TestingAuthenticationToken(userDetails, "cred", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

		// act
		AuthenticationResponse response = service.authenticate("user", "credential");

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user");
	}

	@Test
	void shouldAuthenticateFail() {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user")).thenReturn(false);
		when(authenticationManager.authenticate(any())).thenThrow(IllegalArgumentException.class);

		// act
		AuthenticationResponse response = service.authenticate("user", "credential");

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		assertLogEqualsIgnoreCase(logAppender, "Login failed");
		verify(userLoginAttemptManagerService).isBlocked("user");
		verify(userLoginAttemptManagerService).updateLoginAttempt("user");
	}
	
	@ParameterizedTest
	@MethodSource("nonMfaTestData")
	void shouldAuthenticate(boolean systemLevelMfaEnabled, boolean userMfaEnabled) {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user")).thenReturn(false);
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setMfaEnabled(userMfaEnabled);
		when(authenticationManager.authenticate(any()))
			.thenReturn(new TestingAuthenticationToken(userDetails, "cred", List.of(new SimpleGrantedAuthority("ROLE_USER"))));
		when(userConverter.toUserInfoDto(any(GmsUserDetails.class), eq(false))).thenReturn(TestUtils.createUserInfoDto());
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA)).thenReturn(false);
		when(systemPropertyService.getBoolean(SystemProperty.ENABLE_MFA)).thenReturn(systemLevelMfaEnabled);
		when(tokenGeneratorService.getAuthenticationDetails(any(GmsUserDetails.class))).thenReturn(Map.of(
				JwtConfigType.ACCESS_JWT, "ACCESS_JWT",
				JwtConfigType.REFRESH_JWT, "REFRESH_JWT"
		));
		
		//act
		AuthenticationResponse response = service.authenticate("user", "credential");
		
		// assert
		assertNotNull(response);
		assertEquals("ACCESS_JWT", response.getToken());
		assertEquals("REFRESH_JWT", response.getRefreshToken());
		assertEquals(AuthResponsePhase.COMPLETED, response.getPhase());

		verify(userLoginAttemptManagerService).isBlocked("user");
		verify(userLoginAttemptManagerService).resetLoginAttempt("user");
		verify(authenticationManager).authenticate(any());
		verify(tokenGeneratorService).getAuthenticationDetails(any(GmsUserDetails.class));
		verify(userConverter).toUserInfoDto(any(GmsUserDetails.class), eq(false));
		verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);
		verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_MFA);
	}

	@ParameterizedTest
	@MethodSource("mfaTestData")
	void shouldExpectMfa(boolean enableGlobalMfa, boolean enableMfa) {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user")).thenReturn(false);
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

		verify(userLoginAttemptManagerService).isBlocked("user");
		verify(authenticationManager).authenticate(any());
		verify(userConverter).toUserInfoDto(any(GmsUserDetails.class), eq(true));
		verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);
		verify(systemPropertyService, enableGlobalMfa ? never() : times(1)).getBoolean(SystemProperty.ENABLE_MFA);
	}

	@Test
	void shouldLogout() {
		// act
		service.logout();

		// assert
		assertLogEqualsIgnoreCase(logAppender, "User logged out");
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
