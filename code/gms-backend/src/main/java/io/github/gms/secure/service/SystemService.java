package io.github.gms.secure.service;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.event.RefreshCacheEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemService {

	SystemStatusDto getSystemStatus();
	
	void refreshSystemStatus(RefreshCacheEvent userChangedEvent);
}
