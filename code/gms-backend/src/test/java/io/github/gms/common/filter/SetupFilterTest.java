package io.github.gms.common.filter;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.secure.service.SystemService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SetupFilterTest extends AbstractUnitTest {

	SystemService service;
	SetupFilter filter;

	@BeforeEach
	public void setup() {
		// init
		service = mock(SystemService.class);
		filter = new SetupFilter(service);
	}

	@Test
	@SneakyThrows
	void shouldSetupEnabled() {
		SystemStatusDto mock = SystemStatusDto.builder().status("NEED_SETUP").build();
		when(service.getSystemStatus()).thenReturn(mock);

		// act
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		// assert
		verify(service).getSystemStatus();
		verify(response, never()).sendError(HttpStatus.NOT_FOUND.value(), "System is up and running!");
		verify(filterChain).doFilter(any(), any());
	}

	@Test
	@SneakyThrows
	void shouldBeOk() {
		SystemStatusDto mock = SystemStatusDto.builder().status("OK").build();
		when(service.getSystemStatus()).thenReturn(mock);

		// act
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);

		filter.doFilterInternal(request, response, filterChain);

		// assert
		verify(service).getSystemStatus();
		verify(response).sendError(HttpStatus.NOT_FOUND.value(), "System is up and running!");
		verify(filterChain, never()).doFilter(any(), any());
	}
}
