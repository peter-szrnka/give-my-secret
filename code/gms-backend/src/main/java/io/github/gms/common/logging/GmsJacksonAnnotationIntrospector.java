package io.github.gms.common.logging;


import io.github.gms.common.types.Sensitive;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class GmsJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public Object findSerializer(MapperConfig<?> config, Annotated am) {
        Sensitive annotation = am.getAnnotation(Sensitive.class);
        return (annotation != null) ? MaskSensitiveDataSerializer.class : super.findSerializer(config, am);
    }
}
