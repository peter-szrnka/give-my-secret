package io.github.gms.auth;

import java.util.Date;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.Pair;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserAuthService userAuthService;
	
	@Autowired
	private SystemPropertyService systemPropertyService;

	@Override
	public AuthenticationResponse authenticate(HttpServletRequest request) {
		Cookie jwtTokenCookie = WebUtils.getCookie(request, Constants.ACCESS_JWT_TOKEN);
		
		if (jwtTokenCookie == null) {
			log.info("ACCESS JWT MISSING!");
			return AuthenticationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
		}
		
		String jwtToken = jwtTokenCookie.getValue();
		// JWT processing & authentication
		try {
			String algorithm = systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM);
			
			Claims jwsResult = jwtService.parseJwt(jwtToken, algorithm);
			Pair<HttpStatus, String> validationResult = validateJwt(jwsResult);
			
			if (validationResult.first != HttpStatus.OK) {
				log.warn("Authentication failed: {}", validationResult.second);
				return AuthenticationResponse.builder()
						.responseStatus(validationResult.first)
						.errorMessage(validationResult.second)
						.build();
			}

			UserDetails userDetails = userAuthService.loadUserByUsername(jwsResult.get(MdcParameter.USER_NAME.getDisplayName(), String.class));
			
			if (!userDetails.isEnabled()) {
				log.warn("User is blocked");
				return AuthenticationResponse.builder()
						.responseStatus(HttpStatus.FORBIDDEN)
						.errorMessage("User is blocked")
						.build();
			}
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// Configuration of MDC parameters
			Stream.of(MdcParameter.values())
				.filter(MdcParameter::isInput)
				.forEach(mdcParameter -> MDC.put(mdcParameter.getDisplayName(), jwsResult.get(mdcParameter.getDisplayName())));

			return AuthenticationResponse.builder()
					.authentication(authentication)
					.build();
		} catch (Exception e) {
			log.warn("Authentication failed: {}", e.getMessage());
			return AuthenticationResponse.builder()
					.responseStatus(HttpStatus.FORBIDDEN)
					.errorMessage("Authentication failed!")
					.build();
		}
	}

	private Pair<HttpStatus, String> validateJwt(Claims jwsResult) {
		if (jwsResult.getExpiration().before(new Date())) {
			return Pair.of(HttpStatus.NOT_ACCEPTABLE, "JWT token has expired!");
		}
		
		return Pair.of(HttpStatus.OK, null);
	}
}
