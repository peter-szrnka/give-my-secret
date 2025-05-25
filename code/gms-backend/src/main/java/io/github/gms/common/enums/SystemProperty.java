package io.github.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.gms.common.enums.PropertyType.*;
import static io.github.gms.common.enums.SystemPropertyCategory.*;
import static io.github.gms.common.util.Constants.TRUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum SystemProperty {
	ACCESS_JWT_EXPIRATION_TIME_SECONDS(JWT_TOKEN, LONG, "900", value -> Integer.parseInt(value) > 0),
	ACCESS_JWT_ALGORITHM(JWT_TOKEN, STRING, "HS512"),
	REFRESH_JWT_EXPIRATION_TIME_SECONDS(JWT_TOKEN, LONG, "86400", value -> Integer.parseInt(value) > 0),
	REFRESH_JWT_ALGORITHM(JWT_TOKEN, STRING, "HS512"),
	CSRF_TOKEN_EXPIRATION_TIME_SECONDS(JWT_TOKEN, LONG, "7200", value -> Integer.parseInt(value) > 0),
	ORGANIZATION_NAME(GENERAL, STRING, "NA"),
	ORGANIZATION_CITY(GENERAL, STRING, "NA"),
	// MFA is mandatory for all users
	ENABLE_GLOBAL_MFA(MFA, BOOLEAN, "false"),
	// MFA is enabled for users to use
	ENABLE_MFA(MFA, BOOLEAN, "false"),
	FAILED_ATTEMPTS_LIMIT(GENERAL, INTEGER, "3", value -> Integer.parseInt(value) > 0),
	// Job configurations
	OLD_JOB_ENTRY_LIMIT(JOB, STRING, "1;d"),
	JOB_OLD_MESSAGE_LIMIT(JOB, STRING, "90;d"),
	JOB_OLD_EVENT_LIMIT(JOB, STRING, "180;d"),
	ENABLE_MULTI_NODE(JOB, BOOLEAN, "false"),
	EVENT_MAINTENANCE_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	EVENT_MAINTENANCE_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	JOB_MAINTENANCE_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	JOB_MAINTENANCE_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	KEYSTORE_CLEANUP_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	LDAP_SYNC_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	LDAP_SYNC_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	MESSAGE_CLEANUP_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	MESSAGE_CLEANUP_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	SECRET_ROTATION_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	SECRET_ROTATION_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	USER_ANONYMIZATION_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	USER_ANONYMIZATION_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	USER_DELETION_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	USER_DELETION_JOB_ENABLED(JOB, BOOLEAN, TRUE),
	UNPROCESSED_AUDIT_LOGS_RUNNER_CONTAINER_ID(JOB, STRING, ""),
	UNPROCESSED_AUDIT_LOGS_ENABLED(JOB, BOOLEAN, TRUE),
	// Other configurations
	ENABLE_AUTOMATIC_LOGOUT(GENERAL, BOOLEAN, "false"),
	AUTOMATIC_LOGOUT_TIME_IN_MINUTES(GENERAL, INTEGER, "15", value -> Integer.parseInt(value) >= 15),
	ENABLE_DETAILED_AUDIT(GENERAL, BOOLEAN, "false");

	SystemProperty(SystemPropertyCategory category, PropertyType type, String defaultValue, PropertyTypeValidator validator) {
		this.category = category;
		this.type = type;
		this.defaultValue = defaultValue;
		this.validator = validator;
	}

	private final SystemPropertyCategory category;
	private final PropertyType type;
	private final String defaultValue;
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