package io.github.gms.auth;

import io.github.gms.abstraction.AbstractUnitTest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsCsrfTokenRequestHandlerTest extends AbstractUnitTest {

    private final GmsCsrfTokenRequestHandler gmsCsrfTokenRequestHandler = new GmsCsrfTokenRequestHandler();

    @Test
    void handle_thenSetRequestAttributes() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        CsrfToken mockCsrfToken = mock(CsrfToken.class);
        when(mockCsrfToken.getParameterName()).thenReturn("csrf_token");
        Supplier<CsrfToken> deferredCsrfToken = () -> mockCsrfToken;

        // when
        gmsCsrfTokenRequestHandler.handle(request, response, deferredCsrfToken);

        // then
        verify(request).setAttribute(HttpServletResponse.class.getName(), response);
        verify(request).setAttribute(CsrfToken.class.getName(), mockCsrfToken);
        verify(request).setAttribute("csrf_token", mockCsrfToken);
    }

    @Test
    void resolveCsrfTokenValue_whenTokenIsNull_thenReturnNull() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(request.getHeader(csrfToken.getHeaderName())).thenReturn(null);

        // when
        String result = gmsCsrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);

        // then
        assertNull(result);
        verify(request).getHeader(csrfToken.getHeaderName());
    }

    @Test
    void resolveCsrfTokenValue_whenGetTokenIsNull_thenReturnNull() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(request.getHeader(csrfToken.getHeaderName())).thenReturn("token");
        when(csrfToken.getToken()).thenReturn(null);

        // when
        String result = gmsCsrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);

        // then
        assertNull(result);
    }

    @Test
    void resolveCsrfTokenValue_whenEverythingIsAvailable_thenReturnToken() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(request.getHeader(csrfToken.getHeaderName())).thenReturn("token");
        when(csrfToken.getToken()).thenReturn("token");

        // when
        String result = gmsCsrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);

        // then
        assertEquals("token", result);
    }
}

