package io.github.gms.common.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BasePayloadLogger {

    private final JsonMapper jsonMapper;
    private final JsonMapper sensitiveLoggingJsonMapper;
    private final boolean sensitiveDataMaskingEnabled;
    private final boolean loggingEnabled;

    protected void logPayload(Object body) {
        if (!loggingEnabled) {
            return;
        }

        try {
            log.info("{} logged: {}", scope(), getJsonMapper().writeValueAsString(body));
        } catch (Exception e) {
            log.error("Error while logging {}", scope().toLowerCase(), e);
        }
    }

    private JsonMapper getJsonMapper() {
        return sensitiveDataMaskingEnabled ? sensitiveLoggingJsonMapper : jsonMapper;
    }

    protected abstract String scope();
}
