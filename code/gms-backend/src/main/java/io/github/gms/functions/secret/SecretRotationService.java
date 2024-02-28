package io.github.gms.functions.secret;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretRotationService {

	void rotateSecret(SecretEntity secretEntity);

	void rotateSecretById(Long id);
}
