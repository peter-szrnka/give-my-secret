package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static io.github.gms.common.util.Constants.LOGGING_OBJECT_MAPPER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class ResponseLogger implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;
    private final boolean responseLoggingEnabled;

    public ResponseLogger(
            @Qualifier(LOGGING_OBJECT_MAPPER) ObjectMapper objectMapper,
            @Value("${config.response.logging.enabled}") boolean responseLoggingEnabled) {
        this.objectMapper = objectMapper;
        this.responseLoggingEnabled = responseLoggingEnabled;
    }


    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        logResponse(body);
        return body;
    }

    private void logResponse(Object body) {
        if (!responseLoggingEnabled) {
            return;
        }

        try {
            log.info("Response: {}", objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            log.error("Error while logging response", e);
        }
    }
}
