package io.github.gms.common.logging;

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
import tools.jackson.databind.json.JsonMapper;

import static io.github.gms.common.util.Constants.LOGGING_JSON_MAPPER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ControllerAdvice
public class ResponseLogger extends BasePayloadLogger implements ResponseBodyAdvice<Object> {

    public ResponseLogger(
            JsonMapper jsonMapper,
            @Qualifier(LOGGING_JSON_MAPPER) JsonMapper sensitiveLoggingJsonMapper,
            @Value("${config.logging.enable.sensitive-data-masking}") boolean sensitiveDataMaskingEnabled,
            @Value("${config.response.logging.enabled}") boolean responseLoggingEnabled
    ) {
        super(jsonMapper, sensitiveLoggingJsonMapper, sensitiveDataMaskingEnabled, responseLoggingEnabled);
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

        logPayload(body);
        return body;
    }

    @Override
    protected String scope() {
        return "Response";
    }
}
