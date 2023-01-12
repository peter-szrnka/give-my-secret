package io.github.gms.secure.service;

import io.github.gms.common.model.GenerateJwtRequest;
import io.jsonwebtoken.Claims;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface JwtService {
	
	String generateJwt(GenerateJwtRequest request);

	Claims parseJwt(String jwtToken, String algorithm);
}
