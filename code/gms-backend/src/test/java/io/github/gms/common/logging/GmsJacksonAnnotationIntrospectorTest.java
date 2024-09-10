package io.github.gms.common.logging;

import com.fasterxml.jackson.databind.introspect.Annotated;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.types.Sensitive;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class GmsJacksonAnnotationIntrospectorTest extends AbstractUnitTest {

    private final GmsJacksonAnnotationIntrospector gmsJacksonAnnotationIntrospector = new GmsJacksonAnnotationIntrospector();

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testFindSerializer(boolean sensitive) {
        // arrange
        Annotated am = mock(Annotated.class);
        Sensitive mockSensitive = mock(Sensitive.class);
        when(am.getAnnotation(Sensitive.class)).thenReturn(sensitive ? mockSensitive : null);

        // act
        Object result = gmsJacksonAnnotationIntrospector.findSerializer(am);

        // assert
        assertEquals(sensitive ? MaskSensitiveDataSerializer.class : null, result);
    }
}
