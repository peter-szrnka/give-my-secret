package io.github.gms.auth;

import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.google.gson.Gson;

import io.github.gms.common.dto.ErrorResponseDto;
import io.github.gms.common.enums.MdcParameter;

/**
 * Simple {@link AuthenticationEntryPoint} implementation.
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class GmsAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private Gson gson;
	@Autowired
	private Clock clock;

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {

        ErrorResponseDto dto = new ErrorResponseDto("GmsAuthenticationEntryPoint: " + e.getMessage(), MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()), ZonedDateTime.now(clock));
    	
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
    	httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        httpServletResponse.getWriter().write(gson.toJson(dto));
        
        MDC.remove(MdcParameter.CORRELATION_ID.getDisplayName());
    }
}
