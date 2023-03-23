package io.github.gms.common.abstraction;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@MappedSuperclass
public abstract class AbstractGmsEntity implements Serializable {

	private static final long serialVersionUID = -5706779850849304191L;
}
