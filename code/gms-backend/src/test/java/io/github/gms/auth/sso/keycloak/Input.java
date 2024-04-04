package io.github.gms.auth.sso.keycloak;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class Input {
    private Cookie[] cookies;
}
