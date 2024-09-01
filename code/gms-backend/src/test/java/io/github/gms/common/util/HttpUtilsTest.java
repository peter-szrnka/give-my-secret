package io.github.gms.common.util;

import io.github.gms.abstraction.AbstractUnitTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class HttpUtilsTest extends AbstractUnitTest {

    @Test
    void shouldTestPrivateConstructor() {
        assertPrivateConstructor(HttpUtils.class);
    }

    @Test
    void shouldRetrieveRemoteAddressFromHeaders() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(anyString())).thenReturn(null);
        when(request.getHeader("REMOTE_ADDR")).thenReturn("169.154.0.1");
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn("");

        // act
        String response = HttpUtils.getClientIpAddress(request);

        // assert
        assertEquals("169.154.0.1", response);
    }

    @Test
    void shouldReturnRemoteAddress() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // act
        String response = HttpUtils.getClientIpAddress(request);

        // assert
        assertEquals("127.0.0.1", response);
    }
}
