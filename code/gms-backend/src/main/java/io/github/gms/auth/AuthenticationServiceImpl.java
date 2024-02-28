package io.github.gms.auth;

import dev.samstevens.totp.code.CodeVerifier;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.abstraction.AbstractAuthService;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.converter.GenerateJwtRequestConverter;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class AuthenticationServiceImpl extends AbstractAuthService implements AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final UserConverter converter;
	private final CodeVerifier verifier;
	private final UserService userService;

	public AuthenticationServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            SystemPropertyService systemPropertyService,
            GenerateJwtRequestConverter generateJwtRequestConverter,
            UserConverter converter,
            UserAuthService userAuthService,
            CodeVerifier verifier,
			UserService userService) {
		super(jwtService, systemPropertyService, generateJwtRequestConverter, userAuthService);
		this.authenticationManager = authenticationManager;
		this.converter = converter;
		this.verifier = verifier;
		this.userService = userService;
    }

	@Override
	public AuthenticationResponse authenticate(String username, String credential) {
		try {
			if (userService.isBlocked(username)) {
				return AuthenticationResponse.builder()
						.phase(AuthResponsePhase.BLOCKED)
						.build();
			}

			Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
			GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();

			if (isMfaEnabled(user)) {
				return AuthenticationResponse.builder()
				.currentUser(converter.toUserInfoDto(user, true))
				.phase(AuthResponsePhase.MFA_REQUIRED)
				.build(); 
			} 

			Map<JwtConfigType, String> authenticationDetails = getAuthenticationDetails(user);
			userService.resetLoginAttempt(username);

			return AuthenticationResponse.builder()
				.currentUser(converter.toUserInfoDto(user, false))
				.token(authenticationDetails.get(JwtConfigType.ACCESS_JWT))
				.refreshToken(authenticationDetails.get(JwtConfigType.REFRESH_JWT))
				.phase(AuthResponsePhase.COMPLETED)
				.build();
		} catch (Exception ex) {
			userService.updateLoginAttempt(username);
			log.warn("Login failed", ex);
			return new AuthenticationResponse();
		}
	}

	@Override
	public AuthenticationResponse verify(LoginVerificationRequestDto dto) {
		try {
            if (userService.isBlocked(dto.getUsername())) {
                return AuthenticationResponse.builder()
                        .phase(AuthResponsePhase.BLOCKED)
                        .build();
            }

			GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername(dto.getUsername());

			if (!verifier.isValidCode(userDetails.getMfaSecret(), dto.getVerificationCode())) {
				userService.updateLoginAttempt(dto.getUsername());
				return AuthenticationResponse.builder().phase(AuthResponsePhase.FAILED).build();
			}

			Map<JwtConfigType, String> authenticationDetails = getAuthenticationDetails(userDetails);

			// Verify codes
			return AuthenticationResponse.builder()
				.currentUser(converter.toUserInfoDto(userDetails, false))
				.phase(AuthResponsePhase.COMPLETED)
				.token(authenticationDetails.get(JwtConfigType.ACCESS_JWT))
				.refreshToken(authenticationDetails.get(JwtConfigType.REFRESH_JWT))
				.build();
		} catch (Exception e) {
			return AuthenticationResponse.builder()
				.phase(AuthResponsePhase.FAILED)
				.build();
		}
	}

    private boolean isMfaEnabled(GmsUserDetails userDetails) {
		return systemPropertyService.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA) || 
			(systemPropertyService.getBoolean(SystemProperty.ENABLE_MFA) && userDetails.isMfaEnabled());
	}
}