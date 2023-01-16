package io.github.gms.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

import io.github.gms.auth.model.AuthenticationDetails;
import io.github.gms.auth.model.AuthenticationResponse;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AuthenticationService {

	AuthenticationDetails authenticate(String username, String credential);
	
	/**
	 * - Authorizes the incoming request with JWT
	 * - Configures MDC parameters
	 * - Returns with a {@link Authentication} instance.
	 * 
	 * @param request {@link HttpServletRequest}
	 * @return A new {@link AuthenticationResponse} instance.
	 */
	AuthenticationResponse authorize(HttpServletRequest request);
}