package io.github.gms.common.service;

import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;
import io.jsonwebtoken.Claims;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface JwtService {
	
	Map<JwtConfigType, String> generateJwts(Map<JwtConfigType, GenerateJwtRequest> request);
	
	String generateJwt(GenerateJwtRequest request);

	Claims parseJwt(String jwtToken, String algorithm);
}
