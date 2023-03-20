package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreGeneratorServiceImplTest extends AbstractUnitTest {
	
	private final KeystoreGeneratorServiceImpl service = new KeystoreGeneratorServiceImpl();

	@Test
	void shouldGenerateKeystore() {
		assertEquals("generated.jks", service.generate(new SaveKeystoreRequestDto()));
	}
}