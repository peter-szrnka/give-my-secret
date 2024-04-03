package io.github.gms.common.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MockClientHttpResponse implements ClientHttpResponse {

    @NonNull
    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(200);
    }

    @NonNull
    @Override
    public String getStatusText() throws IOException {
        return "OK";
    }

    @Override
    public void close() {

    }

    @NonNull
    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream("body".getBytes());
    }

    @NonNull
    @Override
    public HttpHeaders getHeaders() {
        return new HttpHeaders();
    }
}
