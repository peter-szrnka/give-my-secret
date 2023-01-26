package io.github.gms.auth;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import io.github.gms.auth.model.AuthenticationDetails;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
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
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserAuthService userAuthService;
	
	@Autowired
	private SystemPropertyService systemPropertyService;
	
	@Autowired
	private GenerateJwtRequestConverter generateJwtRequestConverter;

	@Override
	public AuthenticationDetails authenticate(String username, String credential) {
		Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
		GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();

		return getAuthenticationDetails(user);
	}

	@Override
	public AuthenticationResponse authorize(HttpServletRequest request) {
		Cookie jwtTokenCookie = WebUtils.getCookie(request, Constants.ACCESS_JWT_TOKEN);
		
		if (jwtTokenCookie == null) {
			return AuthenticationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
		}
		
		String jwtToken = jwtTokenCookie.getValue();
		// JWT processing & authentication
		try {
			String algorithm = systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM);
			
			Claims jwsResult = jwtService.parseJwt(jwtToken, algorithm);
			Pair<HttpStatus, String> validationResult = validateJwt(jwsResult);
			
			if (validationResult.getFirst() != HttpStatus.OK) {
				log.warn("Authentication failed: {}", validationResult.getSecond());
				return AuthenticationResponse.builder()
						.responseStatus(validationResult.getFirst())
						.errorMessage(validationResult.getSecond())
						.build();
			}

			GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername(jwsResult.get(MdcParameter.USER_NAME.getDisplayName(), String.class));

			if (!userDetails.isEnabled()) {
				log.warn("User is blocked");
				return AuthenticationResponse.builder()
						.responseStatus(HttpStatus.FORBIDDEN)
						.errorMessage("User is blocked")
						.build();
			}
			
			// Let's refresh the existing tokens
			AuthenticationDetails authenticationDetails = getAuthenticationDetails(userDetails);
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// Configuration of MDC parameters
			Stream.of(MdcParameter.values())
				.filter(MdcParameter::isInput)
				.forEach(mdcParameter -> MDC.put(mdcParameter.getDisplayName(), jwsResult.get(mdcParameter.getDisplayName())));

			return AuthenticationResponse.builder()
					.authentication(authentication)
					.jwtPair(authenticationDetails.getJwtPair())
					.build();
		} catch (Exception e) {
			log.warn("Authentication failed: {}", e.getMessage());
			return AuthenticationResponse.builder()
					.responseStatus(HttpStatus.FORBIDDEN)
					.errorMessage("Authentication failed!")
					.build();
		}
	}
	
	
	private AuthenticationDetails getAuthenticationDetails(GmsUserDetails user) {
		Map<JwtConfigType, GenerateJwtRequest> input = Map.of(
				JwtConfigType.ACCESS_JWT, buildAccessJwtRequest(user.getUserId(), user.getUsername(), 
						user.getAuthorities().stream().map(authority -> UserRole.getByName(authority.getAuthority())).collect(Collectors.toSet())),
				JwtConfigType.REFRESH_JWT, buildRefreshTokenRequest(user.getUsername())
		);
		
		return new AuthenticationDetails(jwtService.generateJwts(input), user);
	}
	
	private GenerateJwtRequest buildRefreshTokenRequest(String userName) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_NAME.getDisplayName(), userName
		);
		return generateJwtRequestConverter.toRequest(JwtConfigType.REFRESH_JWT, userName, claims);
	}

	private GenerateJwtRequest buildAccessJwtRequest(Long userId, String userName, Set<UserRole> roles) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), userId,
				MdcParameter.USER_NAME.getDisplayName(), userName,
				"roles", roles
		);

		return generateJwtRequestConverter.toRequest(JwtConfigType.ACCESS_JWT, userName, claims);
	}

	private static Pair<HttpStatus, String> validateJwt(Claims jwsResult) {
		if (jwsResult.getExpiration().before(new Date())) {
			return Pair.of(HttpStatus.BAD_REQUEST, "JWT token has expired!");
		}
		
		return Pair.of(HttpStatus.OK, null);
	}
}