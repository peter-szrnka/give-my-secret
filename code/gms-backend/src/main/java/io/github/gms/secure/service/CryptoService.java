package io.github.gms.secure.service;

import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface CryptoService {

	void validateKeyStoreFile(SaveKeystoreRequestDto dto, byte[] fileContent);

	String decrypt(SecretEntity entity);

	void encrypt(SecretEntity entity);

}
