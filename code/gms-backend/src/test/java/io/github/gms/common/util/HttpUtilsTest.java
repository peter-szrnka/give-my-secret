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
    void test_whenConstructorCalled_thenSuccessfullyInstantiated() {
        assertPrivateConstructor(HttpUtils.class);
    }

    @Test
    void getClientIpAddress_whenIpAddressIsInHeader_thenReturnIpAddressFromHeader() {
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
    void getClientIpAddress_whenIpAddressIsInServletRequest_thenReturnIpAddress() {
        // arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // act
        String response = HttpUtils.getClientIpAddress(request);

        // assert
        assertEquals("127.0.0.1", response);
    }
}
