package io.github.gms.common.filter;

import static io.github.gms.common.util.Constants.OK;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.gms.functions.system.SystemService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class SetupFilter extends OncePerRequestFilter {

	private final SystemService service;

	public SetupFilter(SystemService service) {
		this.service = service;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if (OK.equals(service.getSystemStatus().getStatus())) {
			response.sendError(HttpStatus.NOT_FOUND.value(), "System is up and running!");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
