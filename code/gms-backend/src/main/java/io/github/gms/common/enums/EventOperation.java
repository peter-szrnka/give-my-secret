package io.github.gms.common.enums;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum EventOperation {

	SETUP,
	SAVE,
	DELETE,
	GET_BY_ID,
	GET_VALUE,
	ROTATE_SECRET_MANUALLY,
	TOGGLE_STATUS,
	TOGGLE_MFA,
	DISABLE_EVENT,
	DOWNLOAD,
	GENERATE_KEYSTORE,
	SYNC_LDAP_USERS_MANUALLY;
}
