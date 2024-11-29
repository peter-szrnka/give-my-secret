package io.github.gms.common.filter;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.functions.system.SystemService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
	void doFilterInternal_whenSystemIsNotReady_thenSetupRequired() {
		SystemStatusDto mock = SystemStatusDto.builder().withStatus(SystemStatus.NEED_SETUP.name()).build();
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
	void doFilterInternal_whenSystemIsReady_thenReturnNotFound() {
		SystemStatusDto mock = SystemStatusDto.builder().withStatus("OK").build();
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
