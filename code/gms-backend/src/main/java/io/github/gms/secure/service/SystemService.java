package io.github.gms.secure.service;

import io.github.gms.common.dto.SystemStatusDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemService {

	SystemStatusDto getSystemStatus();
}
