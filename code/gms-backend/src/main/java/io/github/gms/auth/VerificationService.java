package io.github.gms.auth;

import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.dto.LoginVerificationRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface VerificationService {

    AuthenticationResponse verify(LoginVerificationRequestDto dto);
}
