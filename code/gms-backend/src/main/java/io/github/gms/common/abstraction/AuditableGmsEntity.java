package io.github.gms.common.abstraction;

import io.github.gms.common.db.listener.GmsEntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import java.io.Serial;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@MappedSuperclass
@EntityListeners(GmsEntityListener.class)
public abstract class AuditableGmsEntity extends AbstractGmsEntity {
    @Serial
    private static final long serialVersionUID = -5384814018073435649L;

    public abstract Long getId();
}
