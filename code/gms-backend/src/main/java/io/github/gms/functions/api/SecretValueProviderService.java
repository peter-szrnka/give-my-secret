package io.github.gms.functions.api;

import io.github.gms.functions.secret.SecretEntity;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretValueProviderService {

    Map<String, String> getSecretValue(SecretEntity secretEntity);
}
