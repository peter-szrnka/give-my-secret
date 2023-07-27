package io.github.gms.auth;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeVerifier;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.abstraction.AbstractAuthService;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;

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

	public AuthenticationServiceImpl(
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			SystemPropertyService systemPropertyService,
			GenerateJwtRequestConverter generateJwtRequestConverter,
			UserConverter converter,
			UserAuthService userAuthService,
			CodeVerifier verifier) {
		super(jwtService, systemPropertyService, generateJwtRequestConverter, userAuthService);
		this.authenticationManager = authenticationManager;
		this.converter = converter;
		this.verifier = verifier;
	}

	@Override
	public AuthenticationResponse authenticate(String username, String credential) {
		try {
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

			return AuthenticationResponse.builder()
				.currentUser(converter.toUserInfoDto(user, false))
				.token(authenticationDetails.get(JwtConfigType.ACCESS_JWT))
				.refreshToken(authenticationDetails.get(JwtConfigType.REFRESH_JWT))
				.phase(AuthResponsePhase.COMPLETED)
				.build();
		} catch (Exception ex) {
			log.warn("Login failed", ex);
			return new AuthenticationResponse();
		}
	}

	@Override
	public AuthenticationResponse verify(LoginVerificationRequestDto dto) {
		try {
			GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername(dto.getUsername());

			if (!verifier.isValidCode(userDetails.getMfaSecret(), dto.getVerificationCode())) {
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