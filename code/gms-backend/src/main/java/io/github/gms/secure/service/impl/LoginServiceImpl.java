package io.github.gms.secure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.dto.AuthenticateResponseDto;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.service.JwtService;
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
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserConverter converter;

	@Override
	public AuthenticateResponseDto login(AuthenticateRequestDto dto) {
		try {
            Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getCredential()));

            GmsUserDetails user = (GmsUserDetails) authenticate.getPrincipal();
            return new AuthenticateResponseDto(converter.toUserInfoDto(user), jwtService.generateJwt(user));
        } catch (Exception ex) {
        	log.warn("Login failed", ex);
            return new AuthenticateResponseDto();
        }
	}
}