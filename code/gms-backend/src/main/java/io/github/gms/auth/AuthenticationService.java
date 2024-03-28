package io.github.gms.auth;

import io.github.gms.auth.model.AuthenticationResponse;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AuthenticationService {

	AuthenticationResponse authenticate(String username, String credential);

	void logout();
}