package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.sso.keycloak.service.OAuthService;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.service.KeycloakLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
public class KeycloakLoginServiceImpl implements KeycloakLoginService {

    private final OAuthService oAuthService;
    private final HttpServletRequest httpServletRequest;
    private final KeycloakSettings keycloakSettings;

    @Override
    public Map<String, String> login(String username, String credential) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("audience", keycloakSettings.getRealm());
        requestBody.add("username", username);
        requestBody.add("password", credential);
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("scope", "profile email");

        return oAuthService.callEndpoint(keycloakSettings.getKeycloakTokenUrl(), requestBody, Map.class);
    }

    @Override
    public void logout() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Cookie accessJwtCookie = WebUtils.getCookie(httpServletRequest, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(httpServletRequest, REFRESH_JWT_TOKEN);

        if (accessJwtCookie == null || refreshJwtCookie == null) {
            return;
        }

        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("token", accessJwtCookie.getValue());
        requestBody.add("refresh_token", refreshJwtCookie.getValue());
        oAuthService.callEndpoint(keycloakSettings.getLogoutUrl(), requestBody, Void.class);
    }
}
