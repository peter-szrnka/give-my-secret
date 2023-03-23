package io.github.gms.common.filter;

import com.google.common.collect.Sets;
import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.CookieUtils;
import io.github.gms.secure.service.SystemPropertyService;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static io.github.gms.common.util.Constants.SET_COOKIE;

/**
 * A custom Spring filter used to authorize user by parsing JWT token.
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class SecureHeaderInitializerFilter extends OncePerRequestFilter {
	
	private static final Set<String> IGNORED_URLS = Sets.newHashSet("/secure/.*");
	private final AuthenticationService authenticationService;
	private final SystemPropertyService systemPropertyService;
	private final boolean secure;

	public SecureHeaderInitializerFilter(AuthenticationService authenticationService,
										 SystemPropertyService systemPropertyService,
										 @Value("${config.cookie.secure}") boolean secure) {
		this.authenticationService = authenticationService;
		this.systemPropertyService = systemPropertyService;
		this.secure = secure;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		MDC.put(MdcParameter.CORRELATION_ID.getDisplayName(), UUID.randomUUID().toString());
		response.addHeader("X-CORRELATION-ID", (String) MDC.get(MdcParameter.CORRELATION_ID.getDisplayName()));

		if (shouldSkipUrl(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		AuthenticationResponse authenticationResponse = authenticationService.authorize(request);
		
		if (authenticationResponse.getResponseStatus() != HttpStatus.OK) {
			response.sendError(authenticationResponse.getResponseStatus().value(), authenticationResponse.getErrorMessage());
			return;
		}
		
		String accessCookie = CookieUtils.createCookie(ACCESS_JWT_TOKEN, authenticationResponse.getJwtPair().get(JwtConfigType.ACCESS_JWT),
				systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS), secure).toString();
		String refreshCookie = CookieUtils.createCookie(REFRESH_JWT_TOKEN, authenticationResponse.getJwtPair().get(JwtConfigType.REFRESH_JWT),
				systemPropertyService.getLong(SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS), secure).toString();

		response.addHeader(SET_COOKIE, accessCookie);
		response.addHeader(SET_COOKIE, refreshCookie);

		Authentication authentication = authenticationResponse.getAuthentication();
		boolean admin = authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
		MDC.put(MdcParameter.IS_ADMIN.getDisplayName(), admin);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
		
		MDC.clear();
	}
	
	private boolean shouldSkipUrl(String url) {
		return IGNORED_URLS.stream().noneMatch(urlPattern -> {
			Pattern p = Pattern.compile(urlPattern);
			Matcher matcher = p.matcher(url);
			return matcher.matches();
		});
	}
}
