package io.github.gms.common.filter;

import io.github.gms.common.enums.MdcParameter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * A custom Spring filter to attach a Correlation ID and measure the duration of the request.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestInitializationFilter extends OncePerRequestFilter {

    @Value("${config.logging.response.time.disabled:false}")
    private boolean logResponseTimeDisabled;

    private final Clock clock;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = clock.millis();

        MDC.put(MdcParameter.CORRELATION_ID.getDisplayName(), UUID.randomUUID().toString());
        response.addHeader("X-CORRELATION-ID", MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));

        filterChain.doFilter(request, response);
        logResponseTime(request, startTime);

        MDC.remove(MdcParameter.CORRELATION_ID.getDisplayName());
    }

    private void logResponseTime(HttpServletRequest request, long startTime) {
        if (logResponseTimeDisabled) {
            return;
        }

        log.info("Request: {} took {} ms", kv("uri", request.getRequestURI()), kv("duration", clock.millis() - startTime));
    }
}
