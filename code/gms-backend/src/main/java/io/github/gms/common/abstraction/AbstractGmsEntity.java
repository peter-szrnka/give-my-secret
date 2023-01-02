package io.github.gms.common.abstraction;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@MappedSuperclass
public abstract class AbstractGmsEntity implements Serializable {

	private static final long serialVersionUID = -5706779850849304191L;
}
