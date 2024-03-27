package io.github.gms.auth.sso.keycloak;

import io.github.gms.auth.AuthorizationService;
import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.AuthorizationResponse;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.JwtConfigType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Map;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakAuthorizationServiceImpl implements AuthorizationService {

    private final UserAuthService userAuthService;

    @Override
    public AuthorizationResponse authorize(HttpServletRequest request) {
        Cookie jwtTokenCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);
        Cookie refreshJwtTokenCookie = WebUtils.getCookie(request, REFRESH_JWT_TOKEN);

        if (jwtTokenCookie == null || refreshJwtTokenCookie == null) {
            return AuthorizationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
        }

        GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername("test");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return AuthorizationResponse.builder()
                .authentication(authentication)
                .jwtPair(Map.of(
                        JwtConfigType.ACCESS_JWT, jwtTokenCookie.getValue(),
                        JwtConfigType.REFRESH_JWT, refreshJwtTokenCookie.getValue()
                ))
                .build();
    }
}
