package io.github.gms.common.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum UserRole {

	ROLE_ADMIN,
	ROLE_TECHNICAL,
	ROLE_USER,
	ROLE_VIEWER;
	
	private static final Map<String, UserRole> nameMap;
	
	static {
		nameMap = Stream.of(values()).collect(Collectors.toMap(Enum::name, value -> value));
	}
	
	public static UserRole getByName(String name) {
		return nameMap.get(name);
	}
}
