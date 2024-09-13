package io.github.gms.common.db.converter;

import io.github.gms.abstraction.AbstractUnitTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EncryptedFieldConverterTest extends AbstractUnitTest {
	
	private static final String VALID_SECRET = "YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg=";
	private static final String INVALID_SECRET = "YXNkZmdoamsxMjM0NTY3OGFzZGZnaGprMTIzNDU2Nzg5";
	private static final String ENCRYPTION_IV = "R4nd0mIv1234567!";
	private static final String ENCRYPTED_VALUE = "kEEgTrpbKdiJegJFrAcwBnTujN2s";
	private static final String ORIGINAL_VALUE = "value";
	private EncryptedFieldConverter converter;
	
	@Test
	@SneakyThrows
	void shouldConvertToDatabaseColumnFail() {
		// arrange
		converter = new EncryptedFieldConverter(INVALID_SECRET, ENCRYPTION_IV);
		
		// act & assert
		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
				converter.convertToDatabaseColumn(ORIGINAL_VALUE));
		assertEquals("java.security.InvalidKeyException: Invalid AES key length: 33 bytes", exception.getMessage());
	}

	@Test
	void shouldConvertToDatabaseColumn() {
		// arrange
		converter = new EncryptedFieldConverter(VALID_SECRET, ENCRYPTION_IV);

		// act
		String encryptedValue = converter.convertToDatabaseColumn(ORIGINAL_VALUE);

		// assert
		assertEquals(ENCRYPTED_VALUE, encryptedValue);
	}

	@Test
	@SneakyThrows
	void shouldConvertToEntityAttributeFail() {
		// arrange
		converter = new EncryptedFieldConverter(INVALID_SECRET, ENCRYPTION_IV);

		// act & assert
		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
			converter.convertToEntityAttribute(ENCRYPTED_VALUE));
		assertEquals("java.security.InvalidKeyException: Invalid AES key length: 33 bytes", exception.getMessage());
	}

	@Test
	void shouldConvertToEntityAttribute() {
		// arrange
		converter = new EncryptedFieldConverter(VALID_SECRET, ENCRYPTION_IV);

		// act
		String decryptedValue = converter.convertToEntityAttribute(ENCRYPTED_VALUE);

		// assert
		assertEquals(ORIGINAL_VALUE, decryptedValue);
	}
}
