package io.github.gms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Custom response error handler to supress unnecessary errors during tests.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
public class GmsResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error(response.getStatusText());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        log.error(response.getStatusText());
        return false;
    }
}