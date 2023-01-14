package io.github.gms.auth.model;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import io.github.gms.common.enums.JwtConfigType;
import lombok.Builder;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
public class AuthenticationResponse {

	private Authentication authentication;
	@Builder.Default
	private HttpStatus responseStatus = HttpStatus.OK;
	private String errorMessage;
	private Map<JwtConfigType, String> jwtPair;
}