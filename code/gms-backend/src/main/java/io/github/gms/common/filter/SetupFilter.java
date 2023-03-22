package io.github.gms.common.filter;

import io.github.gms.secure.service.SystemService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static io.github.gms.common.util.Constants.OK;

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
