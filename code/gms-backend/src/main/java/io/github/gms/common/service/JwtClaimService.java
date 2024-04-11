package io.github.gms.common.service;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtClaimService {

    private final JwtService jwtService;
    private final SystemPropertyService systemPropertyService;

    public Claims getClaims(String jwtToken) {
        return jwtService.parseJwt(jwtToken, systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM));
    }
}