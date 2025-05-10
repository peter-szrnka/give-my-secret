package io.github.gms.auth;

import io.github.gms.abstraction.AbstractUnitTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class CsrfTokenServiceTest extends AbstractUnitTest {

    private static final String TOKEN_VALUE = "token-value";
    @Mock
    private CsrfTokenRepository csrfTokenRepository;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private CsrfTokenService service;

    @Test
    void generateCsrfToken_whenCannotLoad_thenGenerateNew() {
        // given
        when(csrfTokenRepository.generateToken(request)).thenReturn(new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", TOKEN_VALUE));

        //when
        String response = service.generateCsrfToken();

        // then
        assertEquals(TOKEN_VALUE, response);
        verify(csrfTokenRepository).generateToken(request);
    }

    @Test
    void generateCsrfToken_whenLoadSucceeded_thenReturnExisting() {
        // given
        when(csrfTokenRepository.loadToken(request)).thenReturn(new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", TOKEN_VALUE));

        //when
        String response = service.generateCsrfToken();

        // then
        assertEquals(TOKEN_VALUE, response);
        verify(csrfTokenRepository).loadToken(request);
        verify(csrfTokenRepository, never()).generateToken(request);
    }
}
