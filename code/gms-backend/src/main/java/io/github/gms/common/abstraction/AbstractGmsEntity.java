package io.github.gms.common.abstraction;

import jakarta.persistence.MappedSuperclass;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@MappedSuperclass
public abstract class AbstractGmsEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 7089530376252111656L;
}
