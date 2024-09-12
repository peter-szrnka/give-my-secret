package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BasePayloadLogger {

    private final ObjectMapper objectMapper;
    private final ObjectMapper sensitiveLoggingObjectMapper;
    private final boolean sensitiveDataMaskingEnabled;
    private final boolean loggingEnabled;

    protected void logPayload(Object body) {
        if (!loggingEnabled) {
            return;
        }

        try {
            log.info("{} logged: {}", scope(), getObjectMapper().writeValueAsString(body));
        } catch (JsonProcessingException e) {
            log.error("Error while logging {}", scope().toLowerCase(), e);
        }
    }

    private ObjectMapper getObjectMapper() {
        return sensitiveDataMaskingEnabled ? sensitiveLoggingObjectMapper : objectMapper;
    }

    protected abstract String scope();
}
