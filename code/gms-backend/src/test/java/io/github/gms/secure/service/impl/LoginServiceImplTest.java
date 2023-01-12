package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link LoginServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class LoginServiceImplTest extends AbstractUnitTest {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;
	
	@Mock
	private UserConverter converter;
	
	@Mock
	private SystemPropertyService systemPropertyService;
	
	@InjectMocks
	private LoginServiceImpl service;

	@Test
	void shouldLoginFail() {
		// arrange
		when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(BadCredentialsException.class);

		// act
		AuthenticateResponseDto response = service.login(new AuthenticateRequestDto("user", "credential"));

		// assert
		assertNull(response.getToken());
	}

	@Test
	void shouldLoginPass() {
		// arrange
		Authentication mockAuthentication = mock(Authentication.class);
		when(mockAuthentication.getPrincipal()).thenReturn(TestUtils.createGmsUser());
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mockAuthentication);
		when(jwtService.generateJwt(any(GenerateJwtRequest.class))).thenReturn("mockJWT");
		when(converter.toUserInfoDto(any(GmsUserDetails.class))).thenReturn(new UserInfoDto());

		// act
		AuthenticateResponseDto response = service.login(new AuthenticateRequestDto("user", "credential"));

		// assert
		assertEquals("mockJWT", response.getToken());
		verify(authenticationManager).authenticate(any(Authentication.class));
		verify(jwtService).generateJwt(any(GenerateJwtRequest.class));
		verify(converter).toUserInfoDto(any(GmsUserDetails.class));
	}
}
