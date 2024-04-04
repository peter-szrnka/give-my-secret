package io.github.gms.auth;

import io.github.gms.auth.model.AuthorizationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Date;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO })
public class AuthorizationServiceImpl implements AuthorizationService {

	private final JwtService jwtService;
	private final TokenGeneratorService tokenGeneratorService;
	private final SystemPropertyService systemPropertyService;
	private final UserAuthService userAuthService;

    @Override
	public AuthorizationResponse authorize(HttpServletRequest request) {
		Cookie jwtTokenCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);
		
		if (jwtTokenCookie == null) {
			return AuthorizationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
		}
		
		String jwtToken = jwtTokenCookie.getValue();
		// JWT processing & authentication
		try {
			String algorithm = systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM);
			
			Claims jwsResult = jwtService.parseJwt(jwtToken, algorithm);
			Pair<HttpStatus, String> validationResult = validateJwt(jwsResult);
			
			if (validationResult.getFirst() != HttpStatus.OK) {
				log.warn("Authentication failed: {}", validationResult.getSecond());
				return AuthorizationResponse.builder()
						.responseStatus(validationResult.getFirst())
						.errorMessage(validationResult.getSecond())
						.build();
			}

			GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername(jwsResult.get(MdcParameter.USER_NAME.getDisplayName(), String.class));

			if (!userDetails.isEnabled()) {
				log.warn("User is blocked");
				return AuthorizationResponse.builder()
						.responseStatus(HttpStatus.FORBIDDEN)
						.errorMessage("User is blocked")
						.build();
			}
			
			// Let's refresh the existing tokens
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// Configuration of MDC parameters
			Stream.of(MdcParameter.values())
				.filter(MdcParameter::isInput)
				.forEach(mdcParameter -> MDC.put(mdcParameter.getDisplayName(), String.valueOf(jwsResult.get(mdcParameter.getDisplayName()))));

			return AuthorizationResponse.builder()
					.authentication(authentication)
					.jwtPair(tokenGeneratorService.getAuthenticationDetails(userDetails))
					.build();
		} catch (Exception e) {
			log.warn("Authorization failed: {}", e.getMessage());
			return AuthorizationResponse.builder()
					.responseStatus(HttpStatus.FORBIDDEN)
					.errorMessage("Authorization failed!")
					.build();
		}
	}

	private static Pair<HttpStatus, String> validateJwt(Claims jwsResult) {
		if (jwsResult.getExpiration().before(new Date())) {
			return Pair.of(HttpStatus.BAD_REQUEST, "JWT token has expired!");
		}
		
		return Pair.of(HttpStatus.OK, "");
	}
}
