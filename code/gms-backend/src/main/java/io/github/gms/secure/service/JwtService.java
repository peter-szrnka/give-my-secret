package io.github.gms.secure.service;

import java.util.Map;

import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;
import io.jsonwebtoken.Claims;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface JwtService {
	
	Map<JwtConfigType, String> generateJwts(Map<JwtConfigType, GenerateJwtRequest> request);
	
	String generateJwt(GenerateJwtRequest request);

	Claims parseJwt(String jwtToken, String algorithm);
}
