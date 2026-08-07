package io.github.gms;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

/**
 * Custom response error handler to suppress unnecessary errors during tests.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
public class GmsResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public void handleError(@NonNull URI url, @NonNull HttpMethod method, @NonNull ClientHttpResponse response) throws IOException {
        log.error(response.getStatusText());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        log.error(response.getStatusText());
        return false;
    }
}