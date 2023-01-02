package io.github.gms.secure.service;

import io.github.gms.common.entity.SecretEntity;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface CryptoService {

	void validateKeyStoreFile(SaveKeystoreRequestDto dto, byte[] fileContent);

	String decrypt(SecretEntity entity);

	void encrypt(SecretEntity entity);

}
