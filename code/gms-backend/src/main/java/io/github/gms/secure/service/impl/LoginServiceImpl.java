package io.github.gms.secure.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.GenerateJwtRequestConverter;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.JwtService;
import io.github.gms.secure.service.LoginService;
import io.github.gms.secure.service.SystemPropertyService;
import io.jsonwebtoken.Claims;
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
	private UserRepository userRepository;
	
	@Autowired
	private SystemPropertyService systemPropertyService;
	
	@Autowired
	private GenerateJwtRequestConverter generateJwtRequestConverter;

	@Override
	public AuthenticateResponseDto login(AuthenticateRequestDto dto) {
		try {
			Authentication authenticate = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getCredential()));

			GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();
			return new AuthenticateResponseDto(converter.toUserInfoDto(user),
					jwtService.generateJwt(buildAccessJwtRequest(user.getUserId(), user.getUsername(), 
							user.getAuthorities().stream().map(authority -> UserRole.getByName(authority.getAuthority())).collect(Collectors.toSet()))),
					jwtService.generateJwt(buildRefreshTokenRequest(user.getUsername())));
		} catch (Exception ex) {
			log.warn("Login failed", ex);
			return new AuthenticateResponseDto();
		}
	}

	@Override
	public String refreshToken(String refreshJwtToken) {
		Claims jwsResult = jwtService.parseJwt(refreshJwtToken, systemPropertyService.get(SystemProperty.REFRESH_JWT_ALGORITHM));

		UserEntity user = userRepository.findByUsername(jwsResult.getSubject()).orElseThrow(() -> new GmsException(Constants.ENTITY_NOT_FOUND));
		return jwtService.generateJwt(buildAccessJwtRequest(
				user.getId(), user.getUsername(), Stream.of(user.getRoles().split(",")).map(role -> UserRole.getByName(role)).collect(Collectors.toSet())));
	}

	private GenerateJwtRequest buildRefreshTokenRequest(String userName) {
		return generateJwtRequestConverter.toRequest(JwtConfigType.REFRESH_JWT, userName, null);
	}

	private GenerateJwtRequest buildAccessJwtRequest(Long userId, String userName, Set<UserRole> roles) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), userId,
				MdcParameter.USER_NAME.getDisplayName(), userName,
				"roles", roles
		);

		return generateJwtRequestConverter.toRequest(JwtConfigType.ACCESS_JWT, userName, claims);
	}
}