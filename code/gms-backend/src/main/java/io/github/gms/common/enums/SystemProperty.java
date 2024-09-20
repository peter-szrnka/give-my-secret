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
	ACCESS_JWT_EXPIRATION_TIME_SECONDS(PropertyType.LONG, "900", value -> Integer.parseInt(value) > 0),
	ACCESS_JWT_ALGORITHM(PropertyType.STRING, "HS512"),
	REFRESH_JWT_EXPIRATION_TIME_SECONDS(PropertyType.LONG, "86400", value -> Integer.parseInt(value) > 0),
	REFRESH_JWT_ALGORITHM(PropertyType.STRING, "HS512"),
	ORGANIZATION_NAME(PropertyType.STRING, "NA"),
	ORGANIZATION_CITY(PropertyType.STRING, "NA"),
	// MFA is mandatory for all users
	ENABLE_GLOBAL_MFA(PropertyType.BOOLEAN, "false"),
	// MFA is enabled for users to use
	ENABLE_MFA(PropertyType.BOOLEAN, "false"),
	FAILED_ATTEMPTS_LIMIT(PropertyType.INTEGER, "3", value -> Integer.parseInt(value) > 0),
	// Job configurations
	JOB_OLD_MESSAGE_LIMIT(PropertyType.STRING, "90;d"),
	JOB_OLD_EVENT_LIMIT(PropertyType.STRING, "180;d"),
	ENABLE_MULTI_NODE(PropertyType.BOOLEAN, "false"),
	EVENT_MAINTENANCE_RUNNER_CONTAINER_ID(PropertyType.STRING, ""),
	KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID(PropertyType.STRING, ""),
	LDAP_SYNC_RUNNER_CONTAINER_ID(PropertyType.STRING, ""),
	MESSAGE_CLEANUP_RUNNER_CONTAINER_ID(PropertyType.STRING, ""),
	SECRET_ROTATION_RUNNER_CONTAINER_ID(PropertyType.STRING, ""),
	USER_DELETION_RUNNER_CONTAINER_ID(PropertyType.STRING, ""),
	ENABLE_AUTOMATIC_LOGOUT(PropertyType.BOOLEAN, "false"),
	AUTOMATIC_LOGOUT_TIME_IN_MINUTES(PropertyType.INTEGER, "15", value -> Integer.parseInt(value) >= 15);

	SystemProperty(PropertyType type, String defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	SystemProperty(PropertyType type, String defaultValue, PropertyTypeValidator validator) {
		this.type = type;
		this.defaultValue = defaultValue;
		this.validator = validator;
	}

	private final String defaultValue;
	private final PropertyType type;
	private PropertyTypeValidator validator = value -> true;

	private static final Map<String, SystemProperty> keyMap = new HashMap<>();

	static {
		for (SystemProperty property : values()) {
			keyMap.put(property.name(), property);
		}
	}

	public static Optional<SystemProperty> getByKey(String key) {
		return Optional.ofNullable(keyMap.get(key));
	}

	@FunctionalInterface
	public interface PropertyTypeValidator {
		boolean validate(String value);
	}
}