package io.github.gms.common.service;

import io.github.gms.common.model.EntityChangeEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface EventProcessorService {

	void disableEntity(EntityChangeEvent event);
}
