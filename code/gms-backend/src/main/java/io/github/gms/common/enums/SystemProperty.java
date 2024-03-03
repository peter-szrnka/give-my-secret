package io.github.gms.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum SystemProperty {
	ACCESS_JWT_EXPIRATION_TIME_SECONDS(PropertyType.LONG, "900"),
	ACCESS_JWT_ALGORITHM(PropertyType.STRING, "HS512"),
	REFRESH_JWT_EXPIRATION_TIME_SECONDS(PropertyType.LONG, "86400"),
	REFRESH_JWT_ALGORITHM(PropertyType.STRING, "HS512"),
	OLD_EVENT_TIME_LIMIT_DAYS(PropertyType.LONG, "1"),
	ORGANIZATION_NAME(PropertyType.STRING, "NA"),
	ORGANIZATION_CITY(PropertyType.STRING, "NA"),
	// MFA is mandatory for all users
	ENABLE_GLOBAL_MFA(PropertyType.BOOLEAN, "false"),
	// MFA is enabled for users to use
	ENABLE_MFA(PropertyType.BOOLEAN, "false"),
	FAILED_ATTEMPTS_LIMIT(PropertyType.INTEGER, "3"),
	// Job configurations
	JOB_OLD_MESSAGE_LIMIT(PropertyType.STRING, "90;d"),
	JOB_OLD_EVENT_LIMIT(PropertyType.STRING, "1;d");

	SystemProperty(PropertyType type, String defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	private final String defaultValue;
	private final PropertyType type;
	
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