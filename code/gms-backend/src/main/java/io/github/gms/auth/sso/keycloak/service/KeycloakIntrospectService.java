package io.github.gms.auth.sso.keycloak.service;

import io.github.gms.auth.sso.keycloak.model.IntrospectResponse;
import org.springframework.http.ResponseEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeycloakIntrospectService {

    ResponseEntity<IntrospectResponse> getUserDetails(String accessToken, String refreshToken);
}
