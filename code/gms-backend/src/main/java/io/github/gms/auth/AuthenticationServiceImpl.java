package io.github.gms.auth;

import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserLoginAttemptManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class AuthenticationServiceImpl implements AuthenticationService {

	private final TokenGeneratorService tokenGeneratorService;
	private final SystemPropertyService systemPropertyService;
	private final AuthenticationManager authenticationManager;
	private final UserConverter converter;
	private final UserLoginAttemptManagerService userLoginAttemptManagerService;
	private final CsrfTokenService csrfTokenService;

	@Override
	public AuthenticationResponse authenticate(String username, String credential) {
		try {
			if (userLoginAttemptManagerService.isBlocked(username)) { // User locked in DB
				return AuthenticationResponse.builder()
						.phase(AuthResponsePhase.BLOCKED)
						.build();
			}

			Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
			GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();

			if (Boolean.FALSE.equals(user.getAccountNonLocked())) { // User locked in LDAP
				return AuthenticationResponse.builder()
						.phase(AuthResponsePhase.BLOCKED)
						.build();
			}

			if (isMfaEnabled(user)) {
				return AuthenticationResponse.builder()
				.currentUser(converter.toUserInfoDto(user, true))
				.phase(AuthResponsePhase.MFA_REQUIRED)
				.build(); 
			} 

			Map<JwtConfigType, String> authenticationDetails = tokenGeneratorService.getAuthenticationDetails(user);
			userLoginAttemptManagerService.resetLoginAttempt(username);

			String csrfToken = csrfTokenService.generateCsrfToken();

			return AuthenticationResponse.builder()
				.currentUser(converter.toUserInfoDto(user, false))
				.token(authenticationDetails.get(JwtConfigType.ACCESS_JWT))
				.refreshToken(authenticationDetails.get(JwtConfigType.REFRESH_JWT))
				.csrfToken(csrfToken)
				.phase(AuthResponsePhase.COMPLETED)
				.build();
		} catch (Exception ex) {
			userLoginAttemptManagerService.updateLoginAttempt(username);
			log.warn("Login failed", ex);
			return new AuthenticationResponse();
		}
	}

	@Override
	public void logout() {
		log.info("User logged out");
	}

	private boolean isMfaEnabled(GmsUserDetails userDetails) {
		return systemPropertyService.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA) || 
			(systemPropertyService.getBoolean(SystemProperty.ENABLE_MFA) && userDetails.isMfaEnabled());
	}
}