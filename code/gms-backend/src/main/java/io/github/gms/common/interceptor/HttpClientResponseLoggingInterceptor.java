package io.github.gms.common.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class HttpClientResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final boolean httpClientLoggingEnabled;

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        if (!httpClientLoggingEnabled) {
            return response;
        }

        log.info("Requested URL: {}", request.getURI());
        String responseString = new String(response.getBody().readAllBytes());
        log.info("Response status: {} body:\r\n{}", response.getStatusCode().value(), responseString);
        return response;
    }
}
