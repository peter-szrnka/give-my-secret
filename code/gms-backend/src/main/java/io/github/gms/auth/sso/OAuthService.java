package io.github.gms.auth.sso;

import org.springframework.util.MultiValueMap;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface OAuthService {

    <T> T callEndpoint(String url, MultiValueMap<String, String> requestBody, Class<T> responseClass);
}
