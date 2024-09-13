package io.github.gms.auth;

import dev.samstevens.totp.code.CodeVerifier;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserLoginAttemptManagerService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class VerificationServiceTest extends AbstractLoggingUnitTest {

    private UserAuthService userAuthService;
    private UserConverter userConverter;
    private CodeVerifier verifier;
    private UserLoginAttemptManagerService userLoginAttemptManagerService;
    private TokenGeneratorService tokenGeneratorService;
    private VerificationService service;

    @Override
    @BeforeEach
	public void setup() {
        super.setup();

        // init
        tokenGeneratorService = mock(TokenGeneratorService.class);
        userAuthService = mock(UserAuthService.class);
        verifier = mock(CodeVerifier.class);
        userLoginAttemptManagerService = mock(UserLoginAttemptManagerService.class);
        userConverter = mock(UserConverter.class);
        service = new VerificationService(userAuthService, userConverter, verifier, userLoginAttemptManagerService, tokenGeneratorService);

        addAppender(AuthenticationServiceImpl.class);
    }

    @Test
	void shouldVerifyThrowsAnError() {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user1")).thenReturn(false);
		LoginVerificationRequestDto dto = TestUtils.createLoginVerificationRequestDto();

		// act
		AuthenticationResponse response = service.verify(dto);

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user1");
	}

	@Test
	void shouldVerifyFailWhenUserIsBlocked() {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user1")).thenReturn(true);

		// act
		AuthenticationResponse response = service.verify(TestUtils.createLoginVerificationRequestDto());

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user1");
	}

	@Test
	void shouldVerifyFailWhenUserIsLockedInLdap() {
		// arrange
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setAccountNonLocked(false);
		when(userLoginAttemptManagerService.isBlocked("user1")).thenReturn(false);
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);

		// act
		AuthenticationResponse response = service.verify(TestUtils.createLoginVerificationRequestDto());

		// assert
		assertNotNull(response);
		assertEquals(AuthResponsePhase.BLOCKED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user1");
	}

	@Test
	void shouldVerifyFail() {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user1")).thenReturn(false);
		LoginVerificationRequestDto dto = TestUtils.createLoginVerificationRequestDto();
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(verifier.isValidCode(anyString(), anyString())).thenReturn(false);

		// act
		AuthenticationResponse response = service.verify(dto);

		// assert
		assertNotNull(response);
		assertNull(response.getCurrentUser());
		assertNull(response.getToken());
		assertNull(response.getRefreshToken());
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user1");
		verify(userLoginAttemptManagerService).updateLoginAttempt("user1");
		verify(tokenGeneratorService, never()).getAuthenticationDetails(any(GmsUserDetails.class));
	}

	@Test
	void shouldVerify() {
		// arrange
		when(userLoginAttemptManagerService.isBlocked("user1")).thenReturn(false);
		LoginVerificationRequestDto dto = TestUtils.createLoginVerificationRequestDto();
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		when(userAuthService.loadUserByUsername(anyString())).thenReturn(userDetails);
		when(verifier.isValidCode(anyString(), anyString())).thenReturn(true);
        when(tokenGeneratorService.getAuthenticationDetails(any(GmsUserDetails.class)))
                .thenReturn(Map.of(
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

		verify(userLoginAttemptManagerService).isBlocked("user1");
		verify(userAuthService).loadUserByUsername(anyString());
		verify(userConverter).toUserInfoDto(any(GmsUserDetails.class), eq(false));
        verify(tokenGeneratorService).getAuthenticationDetails(any(GmsUserDetails.class));
	}
}
