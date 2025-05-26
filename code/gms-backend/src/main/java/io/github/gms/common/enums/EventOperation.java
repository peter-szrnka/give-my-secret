package io.github.gms.common.enums;

import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum EventOperation {
	// Triggered by Entity listener
	INSERT(false),
	UPDATE(false),
	DELETE(false),
	LIST,

	// Triggered by AOP
	SETUP,
	SAVE(false),
	GET_BY_ID,
	GET_VALUE,
	ROTATE_SECRET_MANUALLY,
	TOGGLE_STATUS,
	TOGGLE_MFA,
	DISABLE_EVENT,
	DOWNLOAD,
	GENERATE_KEYSTORE,
	SYNC_LDAP_USERS_MANUALLY,
	REQUEST_USER_ANONYMIZATION,
	REQUEST_USER_DELETION,
	UNKNOWN;

	private final boolean basicAuditCompatible;
	
	EventOperation() {
		basicAuditCompatible = true;
	}

	EventOperation(boolean basicAuditCompatible) {
		this.basicAuditCompatible = basicAuditCompatible;
	}
}
