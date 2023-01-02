package io.github.gms.secure.service;

import io.github.gms.auth.model.GmsUserDetails;
import io.jsonwebtoken.Claims;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface JwtService {
	
	String generateJwt(GmsUserDetails user);

	Claims parseJwt(String jwtToken);
}
