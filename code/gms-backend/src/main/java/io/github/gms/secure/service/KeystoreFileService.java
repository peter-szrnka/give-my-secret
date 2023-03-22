package io.github.gms.secure.service;

import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreFileService {

	long deleteTempKeystoreFiles();

	String generate(SaveKeystoreRequestDto dto);
}