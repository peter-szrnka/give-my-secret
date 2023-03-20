package io.github.gms.secure.service;

import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreGeneratorService {

	String generate(SaveKeystoreRequestDto dto);
}