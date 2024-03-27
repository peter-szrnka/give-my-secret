package io.github.gms.auth.sso.keycloak;

import io.github.gms.auth.AuthenticationService;
import io.github.gms.auth.model.AuthenticationResponse;
import io.github.gms.auth.sso.OAuthService;
import io.github.gms.auth.sso.keycloak.config.KeycloakSettings;
import io.github.gms.auth.types.AuthResponsePhase;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    private final OAuthService oAuthService;
    private final KeycloakSettings keycloakSettings;

    @Override
    public AuthenticationResponse authenticate(String username, String credential) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("audience", keycloakSettings.getRealm());
        requestBody.add("username", username);
        requestBody.add("password", credential);
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        requestBody.add("scope", "profile email");

        Map<String, String> response = oAuthService.callEndpoint(keycloakSettings.getKeycloakTokenUrl(), requestBody, Map.class);

        // TODO Call oauth endpoint to retrieve current user data

        return AuthenticationResponse.builder()
                .phase(AuthResponsePhase.COMPLETED)
                .token(response.get("access_token"))
                .refreshToken(response.get("refresh_token"))
                .build();
    }

    @Override
    public AuthenticationResponse verify(LoginVerificationRequestDto dto) {
        return null;
    }

    @Override
    public void logout() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakSettings.getClientId());
        requestBody.add("client_secret", keycloakSettings.getClientSecret());
        oAuthService.callEndpoint(keycloakSettings.getLogoutUrl(), requestBody, Void.class);
    }
}
