package io.github.gms.secure.service.impl;

import org.springframework.stereotype.Service;

import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.service.KeystoreGeneratorService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class KeystoreGeneratorServiceImpl implements KeystoreGeneratorService {

	@Override
	public String generate(SaveKeystoreRequestDto dto) {
		return "generated.jks";
	}
}