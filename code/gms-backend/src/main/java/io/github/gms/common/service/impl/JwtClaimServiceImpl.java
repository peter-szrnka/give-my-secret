package io.github.gms.common.service.impl;

import org.springframework.stereotype.Service;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.service.JwtClaimService;
import io.github.gms.common.service.JwtService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.jsonwebtoken.Claims;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class JwtClaimServiceImpl implements JwtClaimService {

    private final JwtService jwtService;
    private final SystemPropertyService systemPropertyService;

    public JwtClaimServiceImpl(JwtService jwtService, SystemPropertyService systemPropertyService) {
        this.jwtService = jwtService;
        this.systemPropertyService = systemPropertyService;
    }

    @Override
    public Claims getClaims(String jwtToken) {
        return jwtService.parseJwt(jwtToken, systemPropertyService.get(SystemProperty.ACCESS_JWT_ALGORITHM));
    }
}
