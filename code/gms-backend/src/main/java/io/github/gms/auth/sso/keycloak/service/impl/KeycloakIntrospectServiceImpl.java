package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakIntrospectServiceImpl implements KeycloakIntrospectService {

    private final OAuthService oAuthService;
    private final KeycloakSettings keycloakSettings;

    @Override
    public IntrospectResponse getUserDetails(String accessToken, String refreshToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("token", accessToken);
        requestBody.add("refresh_token", refreshToken);

        return oAuthService.callEndpoint(keycloakSettings.getIntrospectUrl(), requestBody, IntrospectResponse.class);
    }
}
