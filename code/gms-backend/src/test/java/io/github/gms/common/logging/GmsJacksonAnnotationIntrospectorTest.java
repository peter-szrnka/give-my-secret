package io.github.gms.common.logging;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.types.Sensitive;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;

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
    void findSerializer_whenAnnotationIsAvailableOrNot_thenReturnResult(boolean sensitive) {
        // given
        Annotated am = mock(Annotated.class);
        Sensitive mockSensitive = mock(Sensitive.class);
        MapperConfig<?> config = mock(MapperConfig.class);
        when(am.getAnnotation(Sensitive.class)).thenReturn(sensitive ? mockSensitive : null);

        // when
        Object result = gmsJacksonAnnotationIntrospector.findSerializer(config, am);

        // then
        assertEquals(sensitive ? MaskSensitiveDataSerializer.class : null, result);
    }
}

