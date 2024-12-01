package io.github.gms.common.logging;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MaskSensitiveDataSerializerTest extends AbstractUnitTest {

    private final MaskSensitiveDataSerializer maskSensitiveDataSerializer = new MaskSensitiveDataSerializer();

    @Test
    void serialize_whenMockDataProvided_thenReturnSerializedData() throws IOException {
        // arrange
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);

        // act
        maskSensitiveDataSerializer.serialize("test", gen, provider);

        // assert
        verify(gen).writeString("****");
    }
}
