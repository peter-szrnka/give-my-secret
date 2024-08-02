package io.github.gms.common.enums;

import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum ContainerHostType {
    DOCKER,
    KUBERNETES,
    OPENSHIFT,
    SWARM,
    UNKNOWN;

    public static ContainerHostType getContainerHostType(String value) {
        return Stream.of(values()).filter(type -> type.name().equalsIgnoreCase(value)).findFirst().orElse(UNKNOWN);
    }
}
