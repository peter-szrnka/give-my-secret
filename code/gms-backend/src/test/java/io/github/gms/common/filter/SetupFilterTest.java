package io.github.gms.common.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.secure.service.SystemService;
import lombok.SneakyThrows;

/**
 * Unit test of {@link SetupFilter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SetupFilterTest extends AbstractUnitTest {
	
	@Mock
	private SystemService service;

	@InjectMocks
	private SetupFilter filter;
	
	@Test
	@SneakyThrows
	void shouldSetupEnabled() {
		when(service.getSystemStatus()).thenReturn(SystemStatusDto.builder().status("NEED_SETUP").build());
		
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
		when(service.getSystemStatus()).thenReturn(SystemStatusDto.builder().status("OK").build());
		
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