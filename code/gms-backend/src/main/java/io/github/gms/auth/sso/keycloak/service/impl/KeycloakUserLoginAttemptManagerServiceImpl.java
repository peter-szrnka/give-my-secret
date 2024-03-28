package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.functions.user.UserLoginAttemptManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_KEYCLOAK_SSO)
public class KeycloakUserLoginAttemptManagerServiceImpl implements UserLoginAttemptManagerService {

    @Override
    public void updateLoginAttempt(String username) {

    }

    @Override
    public void resetLoginAttempt(String username) {

    }

    @Override
    public boolean isBlocked(String username) {
        return false;
    }
}
