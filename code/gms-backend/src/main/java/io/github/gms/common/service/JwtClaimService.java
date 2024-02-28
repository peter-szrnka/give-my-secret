package io.github.gms.common.service;

import io.jsonwebtoken.Claims;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface JwtClaimService {
    
    Claims getClaims(String jwtToken);
}