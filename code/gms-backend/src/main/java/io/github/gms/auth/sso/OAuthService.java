package io.github.gms.auth.sso;

import io.github.gms.auth.dto.AuthenticateRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface OAuthService {

    void authenticate(AuthenticateRequestDto dto);
}
