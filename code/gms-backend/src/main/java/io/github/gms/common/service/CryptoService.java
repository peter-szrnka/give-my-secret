package io.github.gms.common.service;

import io.github.gms.functions.keystore.SaveKeystoreRequestDto;
import io.github.gms.functions.secret.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface CryptoService {

	void validateKeyStoreFile(SaveKeystoreRequestDto dto, byte[] fileContent);

	String decrypt(SecretEntity entity);

	void encrypt(SecretEntity entity);

}
