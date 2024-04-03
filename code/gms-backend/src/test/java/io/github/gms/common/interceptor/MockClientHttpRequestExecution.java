package io.github.gms.common.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

public class MockClientHttpRequestExecution implements ClientHttpRequestExecution {

    @NonNull
    @Override
    public ClientHttpResponse execute(@NonNull HttpRequest request, @NonNull byte[] body) throws IOException {
        return new MockClientHttpResponse();
    }
}
