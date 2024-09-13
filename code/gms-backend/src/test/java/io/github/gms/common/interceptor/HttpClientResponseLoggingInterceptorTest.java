package io.github.gms.common.interceptor;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;

import static io.github.gms.util.TestUtils.assertLogContains;
import static io.github.gms.util.TestUtils.assertLogMissing;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class HttpClientResponseLoggingInterceptorTest extends AbstractLoggingUnitTest {

    private static final String BODY = "body";
    private HttpClientResponseLoggingInterceptor interceptor;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        interceptor = new HttpClientResponseLoggingInterceptor(true);
        addAppender(HttpClientResponseLoggingInterceptor.class);
    }

    @Test
    void shouldNotLogResponse() throws IOException {
        // arrange
        interceptor = new HttpClientResponseLoggingInterceptor(false);
        HttpRequest request = mock(HttpRequest.class);
        ClientHttpRequestExecution execution = new MockClientHttpRequestExecution();

        // act & assert
        try (ClientHttpResponse response = interceptor.intercept(request, BODY.getBytes(), execution)) {
            assertNotNull(response);
            verify(request, never()).getURI();
            assertLogMissing(logAppender, "Requested URL: http://localhost:5555/api/get");
            assertLogMissing(logAppender, "Response status: 200");
        }
    }

    @Test
    void shouldLogHttpClientResponse() throws IOException {
        // arrange
        HttpRequest request = mock(HttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("http://localhost:5555/api/get"));
        ClientHttpRequestExecution execution = new MockClientHttpRequestExecution();

        // act & assert
        try (ClientHttpResponse response = interceptor.intercept(request, BODY.getBytes(), execution)) {
            assertNotNull(response);
            verify(request).getURI();
            assertLogContains(logAppender, "Requested URL: http://localhost:5555/api/get");
            assertLogContains(logAppender, "Response status: 200");
        }
    }
}
