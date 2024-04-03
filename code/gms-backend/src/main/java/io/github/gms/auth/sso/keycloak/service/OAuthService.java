package io.github.gms.auth.sso.keycloak.service;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface OAuthService {

    <T> ResponseEntity<T> callEndpoint(String url, MultiValueMap<String, String> requestBody, Class<T> responseClass);
}
