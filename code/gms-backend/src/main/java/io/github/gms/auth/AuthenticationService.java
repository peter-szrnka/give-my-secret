package io.github.gms.auth;

import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.common.dto.LoginVerificationRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AuthenticationService {

	AuthenticateResponseDto authenticate(String username, String credential);

	AuthenticateResponseDto verify(LoginVerificationRequestDto dto);
}