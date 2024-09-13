package io.github.gms.auth.sso.keycloak.service.impl;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.util.DemoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeycloakUserLoginAttemptManagerServiceImplTest extends AbstractLoggingUnitTest {

    private KeycloakUserLoginAttemptManagerServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        service = new KeycloakUserLoginAttemptManagerServiceImpl();
        addAppender(KeycloakUserLoginAttemptManagerServiceImpl.class);
    }

    @Test
    void shouldNotUpdateLoginAttempt() {
        service.updateLoginAttempt(DemoData.USERNAME1);

        // assert
        assertLogContains(logAppender, "updateLoginAttempt method will be ignored when Keycloak SSO based security is active");
    }

    @Test
    void shouldNotResetLoginAttempt() {
        service.resetLoginAttempt(DemoData.USERNAME1);

        // assert
        assertLogContains(logAppender, "resetLoginAttempt method will be ignored when Keycloak SSO based security is active");
    }

    @Test
    void shouldNotBlocked() {
        assertFalse(service.isBlocked(DemoData.USERNAME1));
    }
}
