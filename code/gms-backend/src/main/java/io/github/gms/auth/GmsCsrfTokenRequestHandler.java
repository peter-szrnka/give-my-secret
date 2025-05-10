package io.github.gms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
public class GmsCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> deferredCsrfToken) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        Assert.notNull(deferredCsrfToken, "deferredCsrfToken cannot be null");

        request.setAttribute(HttpServletResponse.class.getName(), response);
        CsrfToken csrfToken = deferredCsrfToken.get();
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(csrfToken, "csrfToken cannot be null");

        String tokenFromHeader = request.getHeader(csrfToken.getHeaderName());

        if (tokenFromHeader == null || csrfToken.getToken() == null) {
            return null;
        }

        return tokenFromHeader;
    }
}
