package io.github.gms.auth;

import io.github.gms.auth.model.AuthenticationDetails;
import io.github.gms.auth.model.AuthenticationResponse;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

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