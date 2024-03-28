package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeycloakIntrospectService {

    IntrospectResponse getUserDetails(String accessToken, String refreshToken);
}
