package io.github.gms.secure.service;

import io.github.gms.common.event.EntityDisabledEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventProcessorService {

	void disableEntity(EntityDisabledEvent event);
}
