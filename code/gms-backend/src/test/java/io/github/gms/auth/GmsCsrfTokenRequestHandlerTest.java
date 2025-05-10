package io.github.gms.auth;

import io.github.gms.abstraction.AbstractUnitTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsCsrfTokenRequestHandlerTest extends AbstractUnitTest {

    private final GmsCsrfTokenRequestHandler gmsCsrfTokenRequestHandler = new GmsCsrfTokenRequestHandler();

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
