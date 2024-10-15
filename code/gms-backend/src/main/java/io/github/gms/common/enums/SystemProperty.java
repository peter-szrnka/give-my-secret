package io.github.gms.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.gms.common.enums.PropertyType.*;
import static io.github.gms.common.enums.SystemPropertyCategory.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum SystemProperty {
	ACCESS_JWT_EXPIRATION_TIME_SECONDS(JWT_TOKEN, LONG, "900", value -> Integer.parseInt(value) > 0),
	ACCESS_JWT_ALGORITHM(JWT_TOKEN, STRING, "HS512"),
	REFRESH_JWT_EXPIRATION_TIME_SECONDS(JWT_TOKEN, LONG, "86400", value -> Integer.parseInt(value) > 0),
	REFRESH_JWT_ALGORITHM(JWT_TOKEN, STRING, "HS512"),
	ORGANIZATION_NAME(GENERAL, STRING, "NA"),
	ORGANIZATION_CITY(GENERAL, STRING, "NA"),
	// MFA is mandatory for all users
	ENABLE_GLOBAL_MFA(MFA, BOOLEAN, "false"),
	// MFA is enabled for users to use
	ENABLE_MFA(MFA, BOOLEAN, "false"),
	FAILED_ATTEMPTS_LIMIT(GENERAL, INTEGER, "3", value -> Integer.parseInt(value) > 0),
	// Job configurations
	JOB_OLD_MESSAGE_LIMIT(JOB, STRING, "90;d"),
	JOB_OLD_EVENT_LIMIT(JOB, STRING, "180;d"),
	ENABLE_MULTI_NODE(JOB, BOOLEAN, "false"),
	EVENT_MAINTENANCE_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	EVENT_MAINTENANCE_JOB_ENABLED(JOB, BOOLEAN, "true"),
	KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	KEYSTORE_CLEANUP_JOB_ENABLED(JOB, BOOLEAN, "true"),
	LDAP_SYNC_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	LDAP_SYNC_JOB_ENABLED(JOB, BOOLEAN, "true"),
	MESSAGE_CLEANUP_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	MESSAGE_CLEANUP_JOB_ENABLED(JOB, BOOLEAN, "true"),
	SECRET_ROTATION_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	SECRET_ROTATION_JOB_ENABLED(JOB, BOOLEAN, "true"),
	USER_DELETION_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	USER_DELETION_JOB_ENABLED(JOB, BOOLEAN, "true"),
	// Other configurations
	ENABLE_AUTOMATIC_LOGOUT(GENERAL, BOOLEAN, "false"),
	AUTOMATIC_LOGOUT_TIME_IN_MINUTES(GENERAL, INTEGER, "15", value -> Integer.parseInt(value) >= 15);

	SystemProperty(SystemPropertyCategory category, PropertyType type, String defaultValue) {
		this.category = category;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	SystemProperty(SystemPropertyCategory category, PropertyType type, String defaultValue, PropertyTypeValidator validator) {
		this.category = category;
		this.type = type;
		this.defaultValue = defaultValue;
		this.validator = validator;
	}

	private final String defaultValue;
	private final PropertyType type;
	private SystemPropertyCategory category = SystemPropertyCategory.GENERAL;
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