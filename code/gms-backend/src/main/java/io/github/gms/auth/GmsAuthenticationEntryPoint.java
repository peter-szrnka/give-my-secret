package io.github.gms.auth;

import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Simple {@link AuthenticationEntryPoint} implementation.
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class GmsAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;
	private final Clock clock;

    public GmsAuthenticationEntryPoint(ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {

        ErrorResponseDto dto = new ErrorResponseDto("GmsAuthenticationEntryPoint: " + e.getMessage(), MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()), ZonedDateTime.now(clock));
    	
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
    	httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(dto));
        
        MDC.remove(MdcParameter.CORRELATION_ID.getDisplayName());
    }
}
