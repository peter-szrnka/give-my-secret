package io.github.gms.common.db.converter;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class UnEncryptedFieldConverterTest extends AbstractUnitTest {

    private static final String ORIGINAL_VALUE = "value";

    private final UnEncryptedFieldConverter converter = new UnEncryptedFieldConverter();

    @Test
    void shouldConvertToDatabaseColumn() {
        // act
        String encryptedValue = converter.convertToDatabaseColumn(ORIGINAL_VALUE);

        // assert
        assertEquals(ORIGINAL_VALUE, encryptedValue);
    }


    @Test
    void shouldConvertToEntityAttribute() {
        // act
        String decryptedValue = converter.convertToEntityAttribute(ORIGINAL_VALUE);

        // assert
        assertEquals(ORIGINAL_VALUE, decryptedValue);
    }
}
