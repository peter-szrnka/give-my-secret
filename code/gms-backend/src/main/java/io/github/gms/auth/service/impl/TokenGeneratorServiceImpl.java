package io.github.gms.auth.service.impl;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.auth.service.TokenGeneratorService;
import io.github.gms.common.converter.GenerateJwtRequestConverter;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class TokenGeneratorServiceImpl implements TokenGeneratorService {

    private final JwtService jwtService;
    private final GenerateJwtRequestConverter generateJwtRequestConverter;

    @Override
    public Map<JwtConfigType, String> getAuthenticationDetails(GmsUserDetails user) {
        Map<JwtConfigType, GenerateJwtRequest> input = Map.of(
                JwtConfigType.ACCESS_JWT, buildAccessJwtRequest(user.getUserId(), user.getUsername(),
                        user.getAuthorities().stream().map(authority -> UserRole.getByName(authority.getAuthority())).collect(Collectors.toSet())),
                JwtConfigType.REFRESH_JWT, buildRefreshTokenRequest(user.getUsername())
        );

        return jwtService.generateJwts(input);
    }

    private GenerateJwtRequest buildRefreshTokenRequest(String userName) {
        Map<String, Object> claims = Map.of(
                MdcParameter.USER_NAME.getDisplayName(), userName
        );
        return generateJwtRequestConverter.toRequest(JwtConfigType.REFRESH_JWT, userName, claims);
    }

    private GenerateJwtRequest buildAccessJwtRequest(Long userId, String userName, Set<UserRole> roles) {
        Map<String, Object> claims = Map.of(
                MdcParameter.USER_ID.getDisplayName(), userId,
                MdcParameter.USER_NAME.getDisplayName(), userName,
                "roles", roles
        );

        return generateJwtRequestConverter.toRequest(JwtConfigType.ACCESS_JWT, userName, claims);
    }
}
