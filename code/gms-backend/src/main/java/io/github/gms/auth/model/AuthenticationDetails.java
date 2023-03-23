package io.github.gms.auth.model;

import io.github.gms.common.enums.JwtConfigType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class AuthenticationDetails {

	private Map<JwtConfigType, String> jwtPair;
	private GmsUserDetails user;
}