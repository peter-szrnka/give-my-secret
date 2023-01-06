package io.github.gms.secure.service;

import io.github.gms.common.event.EntityChangeEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventProcessorService {

	void disableEntity(EntityChangeEvent event);
}
