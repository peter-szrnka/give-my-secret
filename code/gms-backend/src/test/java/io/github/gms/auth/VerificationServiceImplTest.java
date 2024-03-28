package io.github.gms.auth;

import ch.qos.logback.classic.Logger;
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
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class VerificationServiceImplTest extends AbstractLoggingUnitTest {

    private UserAuthService userAuthService;
    private UserConverter userConverter;
    private CodeVerifier verifier;
    private UserLoginAttemptManagerService userLoginAttemptManagerService;
    private TokenGeneratorService tokenGeneratorService;
    private VerificationServiceImpl service;

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
        service = new VerificationServiceImpl(userAuthService, userConverter, verifier, userLoginAttemptManagerService, tokenGeneratorService);

        ((Logger) LoggerFactory.getLogger(AuthenticationServiceImpl.class)).addAppender(logAppender);
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
		assertEquals(AuthResponsePhase.FAILED, response.getPhase());
		verify(userLoginAttemptManagerService).isBlocked("user1");
		verify(userLoginAttemptManagerService).updateLoginAttempt("user1");
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
