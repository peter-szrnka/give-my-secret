package io.github.gms.functions.api;

import io.github.gms.functions.secret.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreValidatorService {
    void validateSecretKeystore(SecretEntity secretEntity);
}