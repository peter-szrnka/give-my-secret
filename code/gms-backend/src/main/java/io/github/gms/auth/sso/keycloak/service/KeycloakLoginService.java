package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.LoginResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.WebUtils;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.AUDIENCE;
import static io.github.gms.common.util.Constants.CACHE_KEYCLOAK_SSO_GENERATOR;
import static io.github.gms.common.util.Constants.CACHE_SSO_USER;
import static io.github.gms.common.util.Constants.CLIENT_ID;
import static io.github.gms.common.util.Constants.CLIENT_SECRET;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.CREDENTIAL;
import static io.github.gms.common.util.Constants.GRANT_TYPE;
import static io.github.gms.common.util.Constants.REFRESH_JWT_TOKEN;
import static io.github.gms.common.util.Constants.REFRESH_TOKEN;
import static io.github.gms.common.util.Constants.SCOPE;
import static io.github.gms.common.util.Constants.SCOPE_GMS;
import static io.github.gms.common.util.Constants.TOKEN;
import static io.github.gms.common.util.Constants.USERNAME;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
@CacheConfig(cacheNames = { CACHE_SSO_USER }, keyGenerator = CACHE_KEYCLOAK_SSO_GENERATOR)
public class KeycloakLoginService {

    private final KeycloakOAuthService oAuthService;
    private final HttpServletRequest httpServletRequest;
    private final KeycloakSettings keycloakSettings;

    public ResponseEntity<LoginResponse> login(String username, String credential) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(GRANT_TYPE, CREDENTIAL);
        requestBody.add(AUDIENCE, keycloakSettings.getRealm());
        requestBody.add(USERNAME, username);
        requestBody.add(CREDENTIAL, credential);
        requestBody.add(CLIENT_ID, keycloakSettings.getClientId());
        requestBody.add(CLIENT_SECRET, keycloakSettings.getClientSecret());
        requestBody.add(SCOPE, SCOPE_GMS);

        return oAuthService.callPostEndpoint(keycloakSettings.getKeycloakTokenUrl(), requestBody, LoginResponse.class);
    }

    @CacheEvict
    public void logout() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        Cookie accessJwtCookie = WebUtils.getCookie(httpServletRequest, ACCESS_JWT_TOKEN);
        Cookie refreshJwtCookie = WebUtils.getCookie(httpServletRequest, REFRESH_JWT_TOKEN);

        if (accessJwtCookie == null || refreshJwtCookie == null) {
            return;
        }

        requestBody.add(CLIENT_ID, keycloakSettings.getClientId());
        requestBody.add(CLIENT_SECRET, keycloakSettings.getClientSecret());
        requestBody.add(TOKEN, accessJwtCookie.getValue());
        requestBody.add(REFRESH_TOKEN, refreshJwtCookie.getValue());
        oAuthService.callPostEndpoint(keycloakSettings.getLogoutUrl(), requestBody, Void.class);
    }
}
