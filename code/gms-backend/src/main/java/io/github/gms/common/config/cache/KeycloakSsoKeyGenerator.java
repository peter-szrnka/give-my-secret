package io.github.gms.common.config.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class KeycloakSsoKeyGenerator implements KeyGenerator {

    @Override
    public @NonNull Object generate(@NonNull Object target, @NonNull Method method, Object... params) {
        String accessToken = (String) params[0];
        String refreshToken = (String) params[1];
        String hash = accessToken.concat(refreshToken);
        return hash.hashCode();
    }
}
