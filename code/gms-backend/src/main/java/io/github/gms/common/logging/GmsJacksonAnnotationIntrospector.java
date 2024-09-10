package io.github.gms.common.logging;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.github.gms.common.types.Sensitive;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class GmsJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Object findSerializer(Annotated am) {
        Sensitive annotation = am.getAnnotation(Sensitive.class);
        return (annotation != null) ? MaskSensitiveDataSerializer.class : super.findSerializer(am);
    }
}
