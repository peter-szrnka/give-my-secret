package io.github.gms.auth.model;

import java.util.Map;

import io.github.gms.common.enums.JwtConfigType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationDetails {

	private Map<JwtConfigType, String> jwtPair;
	private GmsUserDetails user;
}