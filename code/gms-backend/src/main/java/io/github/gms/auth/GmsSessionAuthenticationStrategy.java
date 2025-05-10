package io.github.gms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.DeferredCsrfToken;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
public class GmsSessionAuthenticationStrategy implements SessionAuthenticationStrategy {

    private final CsrfTokenRepository tokenRepository;
    private final CsrfTokenRequestHandler requestHandler;

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException {
        CsrfToken token = this.tokenRepository.loadToken(request);

        if (token != null) {
            DeferredCsrfToken deferredCsrfToken = this.tokenRepository.loadDeferredToken(request, response);
            requestHandler.handle(request, response, deferredCsrfToken::get);
        }
    }
}
