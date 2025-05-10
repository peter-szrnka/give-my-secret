package io.github.gms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;

import java.util.function.Supplier;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
public class GmsCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    @Override
    public void handle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Supplier<CsrfToken> deferredCsrfToken) {
        request.setAttribute(HttpServletResponse.class.getName(), response);
        CsrfToken csrfToken = deferredCsrfToken.get();
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(@NonNull HttpServletRequest request, @NonNull CsrfToken csrfToken) {
        String tokenFromHeader = request.getHeader(csrfToken.getHeaderName());

        if (tokenFromHeader == null || csrfToken.getToken() == null) {
            return null;
        }

        return tokenFromHeader;
    }
}
