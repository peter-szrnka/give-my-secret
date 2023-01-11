package io.github.gms.common.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum SystemProperty {
	ACCESS_JWT_EXPIRATION_TIME_SECONDS(PropertyType.LONG, "900"),
	ACCESS_JWT_ALGORITHM(PropertyType.STRING, "HS512"),
	REFRESH_JWT_EXPIRATION_TIME_SECONDS(PropertyType.LONG, "900"),
	REFRESH_JWT_ALGORITHM(PropertyType.STRING, "HS512"),
	OLD_EVENT_TIME_LIMIT_DAYS(PropertyType.LONG, "1");

	private SystemProperty(PropertyType type, String defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	private String defaultValue;
	private PropertyType type;
	
	private static final Map<String, SystemProperty> keyMap = new HashMap<>();
	
	static {
		for (SystemProperty property : values()) {
			keyMap.put(property.name(), property);
		}
	}
	
	public static Optional<SystemProperty> getByKey(String key) {
		return Optional.ofNullable(keyMap.get(key));
	}
}