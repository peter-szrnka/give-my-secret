package io.github.gms.common.filter;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.AuthorizationService;
import io.github.gms.auth.model.AuthorizationResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.util.Constants;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecureHeaderInitializerFilterTest extends AbstractUnitTest {

	private static final String ERROR_MESSAGE = "Error!";

	private AuthorizationService authorizationService;
	private SystemPropertyService systemPropertyService;
	private SecureHeaderInitializerFilter filter;

	@BeforeEach
	public void setup() {
		// init
		authorizationService = mock(AuthorizationService.class);
		systemPropertyService = mock(SystemPropertyService.class);
		filter = new SecureHeaderInitializerFilter(authorizationService, systemPropertyService, false);
	}
	
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
		assertNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		verify(authorizationService, never()).authorize(any(HttpServletRequest.class));
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
		when(authorizationService.authorize(any(HttpServletRequest.class))).thenReturn(AuthorizationResponse.builder()
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
	
	@SuppressWarnings({ "rawtypes" })
	@ParameterizedTest
	@MethodSource("testData")
	@SneakyThrows
	void shouldPass(UserRole role, boolean admin) {
		// arrange
		MockedStatic<MDC>  mockedMDC = mockStatic(MDC.class);
		filter = new SecureHeaderInitializerFilter(authorizationService, systemPropertyService, true);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		Authentication mockAuthentication = mock(Authentication.class);
		when(mockAuthentication.getPrincipal()).thenReturn(TestUtils.createGmsAdminUser());

		when(authorizationService.authorize(any(HttpServletRequest.class))).thenReturn(AuthorizationResponse.builder()
			.responseStatus(HttpStatus.OK)
			.authentication(mockAuthentication)
			.jwtPair(Map.of(JwtConfigType.ACCESS_JWT, "ACCESS_JWT", JwtConfigType.REFRESH_JWT, "REFRESH_JWT"))
			.build());
		
		when(request.getRequestURI()).thenReturn("/secure/apikey/list");
		Set mockAuthorities = Sets.newHashSet(new SimpleGrantedAuthority(role.name()));
		when(mockAuthentication.getAuthorities()).thenReturn(mockAuthorities);
		
		when(systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS))
			.thenReturn(900L);
		when(systemPropertyService.getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS))
			.thenReturn(86400L);

		MockedStatic<CookieUtils> mockCookieUtils = mockStatic(CookieUtils.class);
		ResponseCookie accessJwtCookie = mock(ResponseCookie.class);
        when(accessJwtCookie.toString()).thenReturn("mock-cookie1");
        mockCookieUtils.when(() -> CookieUtils.createCookie(eq(Constants.ACCESS_JWT_TOKEN), eq("ACCESS_JWT"), eq(900L), eq(true))).thenReturn(accessJwtCookie);

        ResponseCookie refreshJwtCookie = mock(ResponseCookie.class);
        when(refreshJwtCookie.toString()).thenReturn("mock-cookie2");
        mockCookieUtils.when(() -> CookieUtils.createCookie(eq(Constants.REFRESH_JWT_TOKEN), eq("REFRESH_JWT"), eq(86400L), eq(true))).thenReturn(refreshJwtCookie);

		// act
		filter.doFilterInternal(request, response, filterChain);
		
		// assert
		assertNull(MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));
		assertEquals(mockAuthentication, SecurityContextHolder.getContext().getAuthentication());
		verify(response).addHeader("Set-Cookie", "mock-cookie1");
		verify(response).addHeader("Set-Cookie", "mock-cookie2");
		verify(filterChain).doFilter(any(), any());
		verify(response, never()).sendError(HttpStatus.BAD_REQUEST.value(), ERROR_MESSAGE);
		verify(authorizationService).authorize(any(HttpServletRequest.class));
		verify(systemPropertyService).getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(systemPropertyService).getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);

		mockedMDC.verify(() -> MDC.put(MdcParameter.IS_ADMIN.getDisplayName(), String.valueOf(admin)));
		mockedMDC.verify(MDC::clear);

		mockCookieUtils.verify(() -> CookieUtils.createCookie(eq(Constants.ACCESS_JWT_TOKEN), eq("ACCESS_JWT"), eq(900L), eq(true)));
        mockCookieUtils.verify(() -> CookieUtils.createCookie(eq(Constants.REFRESH_JWT_TOKEN), eq("REFRESH_JWT"), eq(86400L), eq(true)));
		mockCookieUtils.close();
		mockedMDC.close();
	}

	private static Object[] testData() {
		return new Object[][] {
			{ UserRole.ROLE_USER, false },
			{ UserRole.ROLE_ADMIN, true }
		};
	}
}
