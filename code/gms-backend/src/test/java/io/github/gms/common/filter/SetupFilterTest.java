package io.github.gms.common.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.secure.service.SystemService;

/**
 * Unit test of {@link SetupFilter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
public class SetupFilterTest {

	@Spy
	private SystemService service;

	@InjectMocks
	private SetupFilter filter;

	
	@Test
	void shouldSetupEnabled() {
		try {
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
		} catch(Exception e) {
			assertNotNull(e);
			assertEquals("", e.getMessage());
		}
	}
	
	@Test
	void shouldBeOk() {
		try {
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
		} catch(Exception e) {
			assertNotNull(e);
			assertEquals("", e.getMessage());
		}
	}
}
