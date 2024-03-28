package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.sso.keycloak.converter.KeycloakConverter;
import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import io.github.gms.auth.sso.keycloak.service.KeycloakIntrospectService;
import io.github.gms.auth.sso.keycloak.service.KeycloakLoginService;
import io.github.gms.auth.types.AuthResponsePhase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakAuthenticationServiceImpl implements AuthenticationService {

    private final KeycloakLoginService keycloakLoginService;
    private final KeycloakIntrospectService keycloakIntrospectService;
    private final KeycloakConverter converter;

    @Override
    public AuthenticationResponse authenticate(String username, String credential) {
        // Login with Keycloak
        Map<String, String> response = keycloakLoginService.login(username, credential);

        // Get user data
        IntrospectResponse introspectResponse =
                keycloakIntrospectService.getUserDetails(response.get("access_token"), response.get("refresh_token"));

        return AuthenticationResponse.builder()
                .currentUser(converter.toUserInfoDto(introspectResponse))
                .phase(AuthResponsePhase.COMPLETED)
                .token(response.get("access_token"))
                .refreshToken(response.get("refresh_token"))
                .build();
    }

    @Override
    public void logout() {
        keycloakLoginService.logout();
    }
}
