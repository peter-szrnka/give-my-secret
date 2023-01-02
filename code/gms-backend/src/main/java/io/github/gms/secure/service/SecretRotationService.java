package io.github.gms.secure.service;

import io.github.gms.common.entity.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretRotationService {

	void rotateSecret(SecretEntity secretEntity);

	void rotateSecretById(Long id);
}
