package io.github.gms.common.abstraction;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface GmsClientService extends GmsService {

	void toggleStatus(Long id, boolean enabled);
}
