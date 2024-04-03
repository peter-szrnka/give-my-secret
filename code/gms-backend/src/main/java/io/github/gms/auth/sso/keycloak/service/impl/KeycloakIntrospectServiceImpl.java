package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static io.github.gms.common.util.Constants.CACHE_KEYCLOAK_SSO_GENERATOR;
import static io.github.gms.common.util.Constants.CACHE_SSO_USER;
import static io.github.gms.common.util.Constants.CLIENT_ID;
import static io.github.gms.common.util.Constants.CLIENT_SECRET;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.REFRESH_TOKEN;
import static io.github.gms.common.util.Constants.TOKEN;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { CACHE_SSO_USER }, keyGenerator = CACHE_KEYCLOAK_SSO_GENERATOR)
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakIntrospectServiceImpl implements KeycloakIntrospectService {

    private final OAuthService oAuthService;
    private final KeycloakSettings keycloakSettings;

    @Override
    @Cacheable
    public ResponseEntity<IntrospectResponse> getUserDetails(String accessToken, String refreshToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(CLIENT_ID, keycloakSettings.getClientId());
        requestBody.add(CLIENT_SECRET, keycloakSettings.getClientSecret());
        requestBody.add(TOKEN, accessToken);
        requestBody.add(REFRESH_TOKEN, refreshToken);

        return oAuthService.callEndpoint(keycloakSettings.getIntrospectUrl(), requestBody, IntrospectResponse.class);
    }
}
