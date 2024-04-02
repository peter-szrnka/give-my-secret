package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.model.LoginResponse;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeycloakLoginService {

    LoginResponse login(String username, String credential);

    void logout();
}
