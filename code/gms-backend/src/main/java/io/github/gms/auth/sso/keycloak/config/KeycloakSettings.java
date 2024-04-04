package io.github.gms.auth.sso.keycloak.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

@Getter
@Setter
@Component
@Profile(value = { CONFIG_AUTH_TYPE_KEYCLOAK_SSO })
public class KeycloakSettings {

    @Value("${config.keycloak.tokenUrl}")
    private String keycloakTokenUrl;
    @Value("${config.keycloak.introspectUrl}")
    private String introspectUrl;
    @Value("${config.keycloak.logoutUrl}")
    private String logoutUrl;
    @Value("${config.keycloak.clientId}")
    private String clientId;
    @Value("${config.keycloak.clientSecret}")
    private String clientSecret;
    @Value("${config.keycloak.realm}")
    private String realm;
}
