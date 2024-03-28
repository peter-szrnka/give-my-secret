package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.functions.user.UserLoginAttemptManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_KEYCLOAK_SSO;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_KEYCLOAK_SSO)
public class KeycloakUserLoginAttemptManagerServiceImpl implements UserLoginAttemptManagerService {

    @Override
    public void updateLoginAttempt(String username) {
        log.info("updateLoginAttempt method will be ignored when Keycloak SSO based security is active");
    }

    @Override
    public void resetLoginAttempt(String username) {
        log.info("resetLoginAttempt method will be ignored when Keycloak SSO based security is active");
    }

    @Override
    public boolean isBlocked(String username) {
        return false;
    }
}
