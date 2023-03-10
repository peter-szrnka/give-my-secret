package io.github.gms.common.filter;

import static io.github.gms.common.util.Constants.OK;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.gms.secure.service.SystemService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class SetupFilter extends OncePerRequestFilter {

	@Autowired
	private SystemService service;

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
