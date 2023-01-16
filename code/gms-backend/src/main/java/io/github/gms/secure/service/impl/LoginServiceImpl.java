package io.github.gms.secure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.AuthenticationDetails;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.service.LoginService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserConverter converter;

	@Override
	public AuthenticateResponseDto login(AuthenticateRequestDto dto) {
		try {
			AuthenticationDetails authenticationDetails = authenticationService.authenticate(dto.getUsername(), dto.getCredential());

			return new AuthenticateResponseDto(converter.toUserInfoDto(authenticationDetails.getUser()), 
					authenticationDetails.getJwtPair().get(JwtConfigType.ACCESS_JWT), 
					authenticationDetails.getJwtPair().get(JwtConfigType.REFRESH_JWT));
		} catch (Exception ex) {
			log.warn("Login failed", ex);
			return new AuthenticateResponseDto();
		}
	}
}