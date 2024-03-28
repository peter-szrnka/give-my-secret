package io.github.gms.auth.sso.keycloak.service;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeycloakLoginService {

    Map<String, String> login(String username, String credential);

    void logout();
}
