package io.github.gms.common.db.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.gms.abstraction.AbstractUnitTest;
import lombok.SneakyThrows;

/**
 * Unit test of {@link EncryptedFieldConverter}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EncryptedFieldConverterTest extends AbstractUnitTest {
	
	private static final String ENCRYPTION_IV = "R4nd0mIv1234567!";
	private static final String ENCRYPTED_VALUE = "/jNuDkHGwUeQ/7pSuJ5T1Q==";
	private static final String ORIGINAL_VALUE = "value";
	private EncryptedFieldConverter converter;

	@BeforeEach
	@SneakyThrows
	public void setup() {
		converter = new EncryptedFieldConverter("YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg=", ENCRYPTION_IV);
	}
	
	@Test
	@SneakyThrows
	void shouldConvertToDatabaseColumnFail() {
		converter = new EncryptedFieldConverter("YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg5", ENCRYPTION_IV);
		
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
	@SneakyThrows
	void shouldConvertToEntityAttributeFail() {
		// arrange
		converter = new EncryptedFieldConverter("YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg5", ENCRYPTION_IV);

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
