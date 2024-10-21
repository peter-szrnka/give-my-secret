package io.github.gms.common.enums;

import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum JobStatus {

	COMPLETED,
	FAILED,
	RUNNING;

	public static JobStatus getByName(String name) {
		return Stream.of(values()).filter(item -> item.name().equals(name)).findFirst().orElse(null);
	}
}
