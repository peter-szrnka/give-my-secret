package io.github.gms.common.db.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.gms.abstraction.AbstractUnitTest;
import lombok.SneakyThrows;

/**
 * Unit test of {@link EncryptedFieldConverter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EncryptedFieldConverterTest extends AbstractUnitTest {
	
	private static final String ENCRYPTED_VALUE = "3G8TL6q/R1o6nE51e+0r/g==";
	private static final String ORIGINAL_VALUE = "value";
	private EncryptedFieldConverter converter;

	@BeforeEach
	@SneakyThrows
	public void setup() {
		converter = new EncryptedFieldConverter();
		ReflectionTestUtils.setField(converter, "secret", "YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg=");
	}
	
	@Test
	void shouldConvertToDatabaseColumnFail() {
		ReflectionTestUtils.setField(converter, "secret", "YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg5");
		
		// act & assert
		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
				converter.convertToDatabaseColumn(ORIGINAL_VALUE));
		assertEquals("java.security.InvalidKeyException: Invalid AES key length: 33 bytes", exception.getMessage());
	}
	
	@Test
	void shouldConvertToDatabaseColumn() {
		// act
		String encryptedValue = converter.convertToDatabaseColumn(ORIGINAL_VALUE);
		
		// assert
		assertEquals(ENCRYPTED_VALUE, encryptedValue);
	}
	
	@Test
	void shouldConvertToEntityAttributeFail() {
		ReflectionTestUtils.setField(converter, "secret", "YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg5");
		
		// act & assert
		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
			converter.convertToEntityAttribute(ENCRYPTED_VALUE));
		assertEquals("java.security.InvalidKeyException: Invalid AES key length: 33 bytes", exception.getMessage());
	}
	
	@Test
	void shouldConvertToEntityAttribute() {
		// act
		String decryptedValue = converter.convertToEntityAttribute(ENCRYPTED_VALUE);
		
		// assert
		assertEquals(ORIGINAL_VALUE, decryptedValue);
	}
}
