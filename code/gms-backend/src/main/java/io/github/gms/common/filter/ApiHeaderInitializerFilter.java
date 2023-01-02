package io.github.gms.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.gms.common.enums.HeaderType;
import io.github.gms.common.exception.GmsException;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class ApiHeaderInitializerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String apiKeyValue = request.getHeader(HeaderType.API_KEY.getHeaderName());
		
		if (!StringUtils.hasText(apiKeyValue)) {
			throw new GmsException("API key is missing!");
		}
		
		MDC.put(HeaderType.API_KEY.getMappedName(), apiKeyValue);
		
		filterChain.doFilter(request, response);
	}
}
