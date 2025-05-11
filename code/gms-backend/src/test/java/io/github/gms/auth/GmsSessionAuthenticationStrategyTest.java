package io.github.gms.auth;

import io.github.gms.abstraction.AbstractUnitTest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.DeferredCsrfToken;

import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsSessionAuthenticationStrategyTest extends AbstractUnitTest {

    @Mock
    private CsrfTokenRepository tokenRepository;
    @Mock
    private CsrfTokenRequestHandler requestHandler;
    @InjectMocks
    private GmsSessionAuthenticationStrategy strategy;

    @Test
    void onAuthentication_whenTokenIsNull_thenDoNothing() {
        // Arrange
        var authentication = mock(Authentication.class);
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);

        when(tokenRepository.loadToken(request)).thenReturn(null);

        // Act
        strategy.onAuthentication(authentication, request, response);

        // Assert
        verify(tokenRepository, never()).loadDeferredToken(request, response);
        verify(requestHandler, never()).handle(eq(request), eq(response), any());
    }

    @Test
    void onAuthentication_whenTokenIsNotNull_thenHandleRequest() {
        // Arrange
        var authentication = mock(Authentication.class);
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);

        CsrfToken token = mock(CsrfToken.class);
        DeferredCsrfToken deferredToken = mock(DeferredCsrfToken.class);

        when(tokenRepository.loadToken(request)).thenReturn(token);
        when(tokenRepository.loadDeferredToken(request, response)).thenReturn(deferredToken);

        // Act
        strategy.onAuthentication(authentication, request, response);

        // Assert
        verify(tokenRepository).loadDeferredToken(request, response);
        verify(requestHandler).handle(eq(request), eq(response), any());
    }
}
