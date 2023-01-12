package io.github.gms.secure.service.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.LoginService;
import io.github.gms.secure.service.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserConverter converter;

	@Autowired
	private SystemPropertyService systemPropertyService;

	@Override
	public AuthenticateResponseDto login(AuthenticateRequestDto dto) {
		try {
			Authentication authenticate = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getCredential()));

			GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();
			return new AuthenticateResponseDto(converter.toUserInfoDto(user),
					jwtService.generateJwt(buildGenerateJwtRequest(user)));
		} catch (Exception ex) {
			log.warn("Login failed", ex);
			return new AuthenticateResponseDto();
		}
	}

	private GenerateJwtRequest buildGenerateJwtRequest(GmsUserDetails user) {
		String algorithm = systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM);
		Long expirationDateInSeconds = systemPropertyService.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), user.getUserId(),
				MdcParameter.USER_NAME.getDisplayName(), user.getUsername(),
				"roles", user.getAuthorities().stream() .map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
		);

		return GenerateJwtRequest.builder().subject(user.getUsername()).algorithm(algorithm)
				.expirationDateInSeconds(expirationDateInSeconds)
				.claims(claims)
				.build();
	}
}