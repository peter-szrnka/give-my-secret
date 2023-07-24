package io.github.gms.auth;

import org.springframework.security.core.Authentication;

import io.github.gms.auth.model.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AuthorizationService {
    
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