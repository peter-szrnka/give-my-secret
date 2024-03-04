package io.github.gms.common.filter;

import io.github.gms.functions.system.SystemService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static io.github.gms.common.util.Constants.OK;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class SetupFilter extends OncePerRequestFilter {

	private final SystemService service;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		
		if (OK.equals(service.getSystemStatus().getStatus())) {
			response.sendError(HttpStatus.NOT_FOUND.value(), "System is up and running!");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
