package io.github.gms.api.service;

import io.github.gms.secure.entity.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreValidatorService {
    void validateSecretKeystore(SecretEntity secretEntity);
}