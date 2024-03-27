package io.github.gms.auth.sso.keycloak;

import io.github.gms.auth.AuthorizationService;
import io.github.gms.auth.model.AuthorizationResponse;
import io.github.gms.auth.sso.OAuthService;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.functions.user.UserConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    //private final UserAuthService userAuthService;
    private final OAuthService oAuthService;
    private final UserConverter userConverter;
    private final KeycloakSettings keycloakSettings;

    @Override
    public AuthorizationResponse authorize(HttpServletRequest request) {
        Cookie accessJwtCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(request, REFRESH_JWT_TOKEN);

        if (accessJwtCookie == null || refreshJwtCookie == null) {
            return AuthorizationResponse.builder().responseStatus(HttpStatus.FORBIDDEN).errorMessage("Access denied!").build();
        }

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("token", accessJwtCookie.getValue());
        requestBody.add("refresh_token", refreshJwtCookie.getValue());

        IntrospectResponse response = oAuthService.callEndpoint(keycloakSettings.getIntrospectUrl(), requestBody, IntrospectResponse.class);

        UserDetails userDetails = userConverter.toUserDetails(response);
        //GmsUserDetails userDetails = (GmsUserDetails) userAuthService.loadUserByUsername("test");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return AuthorizationResponse.builder()
                .authentication(authentication)
                .jwtPair(Map.of(
                        JwtConfigType.ACCESS_JWT, accessJwtCookie.getValue(),
                        JwtConfigType.REFRESH_JWT, refreshJwtCookie.getValue()
                ))
                .build();
    }
}
