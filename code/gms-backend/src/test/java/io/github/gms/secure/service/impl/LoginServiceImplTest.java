package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.AuthenticationDetails;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LoginServiceImplTest extends AbstractUnitTest {

	@Mock
	private AuthenticationService authenticationService;

	@Mock
	private UserConverter converter;

	@InjectMocks
	private LoginServiceImpl service;

	@BeforeEach
	public void setup() {
		authenticationService = mock(AuthenticationService.class);
		converter = mock(UserConverter.class);
		service = new LoginServiceImpl(authenticationService, converter);
	}

	@Test
	void shouldLoginFail() {
		// arrange
		when(authenticationService.authenticate(anyString(), anyString())).thenThrow(BadCredentialsException.class);

		// act
		AuthenticateResponseDto response = service.login(new AuthenticateRequestDto("user", "credential"));

		// assert
		assertNull(response.getToken());
		verify(authenticationService).authenticate(anyString(), anyString());
	}

	@Test
	void shouldLoginPass() {
		// arrange
		when(authenticationService.authenticate(anyString(), anyString()))
			.thenReturn(new AuthenticationDetails(Map.of(JwtConfigType.ACCESS_JWT, "mockJWT", JwtConfigType.REFRESH_JWT, "mockRefreshJWT"), TestUtils.createGmsUser()));
		when(converter.toUserInfoDto(any(GmsUserDetails.class))).thenReturn(new UserInfoDto());

		// act
		AuthenticateResponseDto response = service.login(new AuthenticateRequestDto("user", "credential"));

		// assert
		assertEquals("mockJWT", response.getToken());
		assertEquals("mockRefreshJWT", response.getRefreshToken());
		verify(authenticationService).authenticate(anyString(), anyString());
		verify(converter).toUserInfoDto(any(GmsUserDetails.class));
	}
}
