package io.github.gms.common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Type;

import static io.github.gms.common.util.Constants.LOGGING_OBJECT_MAPPER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ControllerAdvice
public class RequestLogger extends BasePayloadLogger implements RequestBodyAdvice {

    public RequestLogger(
            ObjectMapper objectMapper,
            @Qualifier(LOGGING_OBJECT_MAPPER) ObjectMapper sensitiveLoggingObjectMapper,
            @Value("${config.logging.enable.sensitive-data-masking}") boolean sensitiveDataMaskingEnabled,
            @Value("${config.request.logging.enabled}") boolean requestLoggingEnabled) {
        super(objectMapper, sensitiveLoggingObjectMapper, sensitiveDataMaskingEnabled, requestLoggingEnabled);
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

        logPayload(body);
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

    @Override
    protected String scope() {
        return "Request";
    }
}
