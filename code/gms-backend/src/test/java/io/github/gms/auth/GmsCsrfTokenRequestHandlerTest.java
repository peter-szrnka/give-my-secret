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
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        CsrfToken mockCsrfToken = mock(CsrfToken.class);
        when(mockCsrfToken.getParameterName()).thenReturn("csrf_token");
        Supplier<CsrfToken> deferredCsrfToken = () -> mockCsrfToken;

        // Act
        gmsCsrfTokenRequestHandler.handle(request, response, deferredCsrfToken);

        // Assert
        verify(request).setAttribute(HttpServletResponse.class.getName(), response);
        verify(request).setAttribute(CsrfToken.class.getName(), mockCsrfToken);
    }

    @Test
    void resolveCsrfTokenValue_whenTokenIsNull_thenReturnNull() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(request.getHeader(csrfToken.getHeaderName())).thenReturn(null);

        // Act
        String result = gmsCsrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);

        // Assert
        assertNull(result);
    }

    @Test
    void resolveCsrfTokenValue_whenGetTokenIsNull_thenReturnNull() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(request.getHeader(csrfToken.getHeaderName())).thenReturn("token");
        when(csrfToken.getToken()).thenReturn(null);

        // Act
        String result = gmsCsrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);

        // Assert
        assertNull(result);
    }

    @Test
    void resolveCsrfTokenValue_whenEverythingIsAvailable_thenReturnToken() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);

        when(request.getHeader(csrfToken.getHeaderName())).thenReturn("token");
        when(csrfToken.getToken()).thenReturn("token");

        // Act
        String result = gmsCsrfTokenRequestHandler.resolveCsrfTokenValue(request, csrfToken);

        // Assert
        assertEquals("token", result);
    }
}
