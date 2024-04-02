package io.github.gms.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Slf4j
public class HttpClientInterceptor implements ClientHttpRequestInterceptor {

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        String responseString = new String(response.getBody().readAllBytes());
        log.info("URL: {}", request.getURI());
        log.info("Response status: {} body:\n================\r\n{}\r\n================", response.getStatusCode().value(), responseString);
        return response;
    }
}
