package io.github.gms.common.enums;

import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum EntityStatus {

	ACTIVE,
	BLOCKED,
	DELETE_REQUESTED,
	DISABLED,
	INITIAL,
	TO_BE_DELETED;

	public static EntityStatus getByName(String name) {
		return Stream.of(values()).filter(item -> item.name().equals(name)).findFirst().orElse(null);
	}
}
