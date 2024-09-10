package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Type;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class RequestLogger implements RequestBodyAdvice {

    private final ObjectMapper objectMapper;
    private final boolean requestLoggingEnabled;

    public RequestLogger(
            @Qualifier("loggingObjectMapper") ObjectMapper objectMapper,
            @Value("${config.request.logging.enabled}") boolean requestLoggingEnabled) {
        this.objectMapper = objectMapper;
        this.requestLoggingEnabled = requestLoggingEnabled;
    }

    @Override
    public boolean supports(
            @NonNull MethodParameter methodParameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public @NonNull HttpInputMessage beforeBodyRead(
            @NonNull HttpInputMessage inputMessage,
            @NonNull MethodParameter parameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return inputMessage;
    }

    @Override
    public @NonNull Object afterBodyRead(
            @NonNull Object body,
            @NonNull HttpInputMessage inputMessage,
            @NonNull MethodParameter parameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {

        logRequest(body);
        return body;
    }

    @Override
    public Object handleEmptyBody(
            Object body,
            @NonNull HttpInputMessage inputMessage,
            @NonNull MethodParameter parameter,
            @NonNull Type targetType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    private void logRequest(Object body) {
        if (!requestLoggingEnabled) {
            return;
        }

        try {
            log.info("Request logged: {}", objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            log.error("Error while logging request", e);
        }
    }
}
