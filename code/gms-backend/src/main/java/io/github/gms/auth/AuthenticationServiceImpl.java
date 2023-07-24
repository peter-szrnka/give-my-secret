package io.github.gms.auth;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.enums.JwtConfigType;
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
public class AuthenticationServiceImpl extends AbstractAuthServiceImpl implements AuthenticationService {

	private final UserConverter converter;

	public AuthenticationServiceImpl(
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			SystemPropertyService systemPropertyService,
			GenerateJwtRequestConverter generateJwtRequestConverter,
			UserConverter converter) {
		super(authenticationManager, jwtService, systemPropertyService, generateJwtRequestConverter);
		this.converter = converter;
	}

	@Override
	public AuthenticateResponseDto authenticate(String username, String credential) {
		try {
			Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, credential));
			GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();
			Map<JwtConfigType, String> authenticationDetails = getAuthenticationDetails(user);

			return AuthenticateResponseDto.builder()
				.currentUser(converter.toUserInfoDto(user))
				.token(authenticationDetails.get(JwtConfigType.ACCESS_JWT))
				.refreshToken(authenticationDetails.get(JwtConfigType.REFRESH_JWT))
				.phase(isMfaEnabled(user) ? AuthResponsePhase.MFA_REQUIRED : AuthResponsePhase.COMPLETED)
				.build();
		} catch (Exception ex) {
			log.warn("Login failed", ex);
			return new AuthenticateResponseDto();
		}
	}

	@Override
	public AuthenticateResponseDto verify(LoginVerificationRequestDto dto) {
		// TODO Complete
		// Verify codes
		return AuthenticateResponseDto.builder()
				.phase(AuthResponsePhase.COMPLETED)
				.build();
	}
}