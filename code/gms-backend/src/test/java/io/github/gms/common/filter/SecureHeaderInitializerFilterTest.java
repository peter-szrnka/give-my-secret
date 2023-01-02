package io.github.gms.common.filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import lombok.SneakyThrows;

/**
 * Unit test of {@link SecureHeaderInitializerFilter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SecureHeaderInitializerFilterTest extends AbstractUnitTest {

	private static final String ERROR_MESSAGE = "Error!";

	@Mock
	private AuthenticationService authenticationService;

	@InjectMocks
	private SecureHeaderInitializerFilter filter;
	
	@Test
	@SneakyThrows
	void shouldSkip() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		when(request.getRequestURI()).thenReturn("/healthcheck");
		
		// act
		filter.doFilterInternal(request, response, filterChain);
		
		// assert
		assertNotNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(authenticationService, never()).authenticate(any(HttpServletRequest.class));
		verify(filterChain).doFilter(any(), any());
		verify(response, never()).sendError(HttpStatus.OK.value(), ERROR_MESSAGE);
	}
	
	@Test
	@SneakyThrows
	void shouldSendError() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		when(request.getRequestURI()).thenReturn("/secure/apikey/list");
		when(authenticationService.authenticate(any(HttpServletRequest.class))).thenReturn(AuthenticationResponse.builder()
				.responseStatus(HttpStatus.BAD_REQUEST)
				.errorMessage(ERROR_MESSAGE)
				.build());

		// act
		filter.doFilterInternal(request, response, filterChain);
		
		// assert
		assertNotNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(filterChain, never()).doFilter(any(), any());
		verify(response).sendError(HttpStatus.BAD_REQUEST.value(), ERROR_MESSAGE);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@SneakyThrows
	void shouldPass() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		Authentication mockAuthentication = mock(Authentication.class);

		when(authenticationService.authenticate(any(HttpServletRequest.class))).thenReturn(AuthenticationResponse.builder()
				.responseStatus(HttpStatus.OK)
				.authentication(mockAuthentication)
				.build());
		
		when(request.getRequestURI()).thenReturn("/secure/apikey/list");
		Set mockAuthorities = Sets.newHashSet(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()));
		when(mockAuthentication.getAuthorities()).thenReturn(mockAuthorities);

		// act
		filter.doFilterInternal(request, response, filterChain);
		
		// assert
		assertNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(filterChain).doFilter(any(), any());
		verify(response, never()).sendError(HttpStatus.BAD_REQUEST.value(), ERROR_MESSAGE);
	}
}
