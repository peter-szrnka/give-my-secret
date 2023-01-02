package io.github.gms.secure.service;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface LoginService {

	AuthenticateResponseDto login(AuthenticateRequestDto dto);
}
