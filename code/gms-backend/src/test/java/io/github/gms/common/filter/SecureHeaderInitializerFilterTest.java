package io.github.gms.common.filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Sets;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.UserRole;
import io.github.gms.secure.service.SystemPropertyService;
import lombok.SneakyThrows;

/**
 * Unit test of {@link SecureHeaderInitializerFilter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled("Temporarily disabled")
class SecureHeaderInitializerFilterTest extends AbstractUnitTest {

	private static final String ERROR_MESSAGE = "Error!";

	@Mock
	private AuthenticationService authenticationService;
	
	@Mock
	private SystemPropertyService systemPropertyService;

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
		verify(authenticationService, never()).authorize(any(HttpServletRequest.class));
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
		when(authenticationService.authorize(any(HttpServletRequest.class))).thenReturn(AuthenticationResponse.builder()
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
		ReflectionTestUtils.setField(filter, "secure", true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		Authentication mockAuthentication = mock(Authentication.class);

		when(authenticationService.authorize(any(HttpServletRequest.class))).thenReturn(AuthenticationResponse.builder()
				.responseStatus(HttpStatus.OK)
				.authentication(mockAuthentication)
				.jwtPair(Map.of(JwtConfigType.ACCESS_JWT, "ACCESS_JWT", JwtConfigType.REFRESH_JWT, "REFRESH_JWT"))
				.build());
		
		when(request.getRequestURI()).thenReturn("/secure/apikey/list");
		Set mockAuthorities = Sets.newHashSet(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()));
		when(mockAuthentication.getAuthorities()).thenReturn(mockAuthorities);
		
		when(systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS))
			.thenReturn(900L);
		when(systemPropertyService.getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS))
			.thenReturn(86400L);

		// act
		filter.doFilterInternal(request, response, filterChain);
		
		// assert
		assertNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(filterChain).doFilter(any(), any());
		verify(response, never()).sendError(HttpStatus.BAD_REQUEST.value(), ERROR_MESSAGE);
		verify(authenticationService).authorize(any(HttpServletRequest.class));
		verify(systemPropertyService).getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(systemPropertyService).getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);
	}
}
