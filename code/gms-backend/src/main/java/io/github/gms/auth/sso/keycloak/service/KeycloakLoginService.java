package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.model.LoginResponse;
import org.springframework.http.ResponseEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeycloakLoginService {

    ResponseEntity<LoginResponse> login(String username, String credential);

    void logout();
}
