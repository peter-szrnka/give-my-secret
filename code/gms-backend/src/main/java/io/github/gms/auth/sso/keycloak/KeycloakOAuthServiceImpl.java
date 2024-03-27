package io.github.gms.auth.sso.keycloak;

import io.github.gms.auth.dto.AuthenticateRequestDto;
import io.github.gms.auth.sso.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakOAuthServiceImpl implements OAuthService {

    private final RestTemplate restTemplate;

    @Value("${config.keycloak.baseUrl}")
    private final String keycloakBaseUrl;
    @Value("${config.keycloak.clientId}")
    private final String clientId;
    @Value("${config.keycloak.clientSecret}")
    private final String clientSecret;
    @Value("${config.keycloak.realm}")
    private final String realm;

    @Override
    public void authenticate(AuthenticateRequestDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("audience", realm);
        requestBody.add("username", dto.getUsername());
        requestBody.add("password", dto.getCredential());
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate
                .postForEntity(keycloakBaseUrl + "/", requestEntity, String.class);
    }
}
