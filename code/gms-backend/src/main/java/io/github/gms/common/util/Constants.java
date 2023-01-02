package io.github.gms.common.util;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public final class Constants {
	
	private Constants() {}
	
	public static final String OK = "OK";

	public static final String ENTITY_NOT_FOUND = "Entity not found!";
	public static final String LDAP_CRYPT_PREFIX = "{CRYPT}";

	// Roles
	public static final String ALL_ROLE = "hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_VIEWER')";
	public static final String ROLE_USER_OR_VIEWER = "hasAnyRole('ROLE_USER','ROLE_VIEWER')";
	
	// Properties
	public static final String CONFIG_AUTH_TYPE_DB = "db";
	public static final String CONFIG_AUTH_TYPE_LDAP = "ldap";
	public static final String CONFIG_AUTH_TYPE_LDAP_TEST = "ldaptest";
	public static final String CONFIG_LDAP_PASSWORD_ENCODER = "config.ldap.passwordencoder";
	
	// LDAP
	public static final String LDAP_PROPERTY_CN = "cn";
	public static final String LDAP_PROPERTY_UID = "uid";
	public static final String LDAP_PROPERTY_CREDENTIAL = "gmsPassword";
	public static final String LDAP_PROPERTY_ROLE = "gmsRole";
	public static final String LDAP_PROPERTY_EMAIL = "email";
	public static final String PASSWORD_ENCODER = "passwordEncoder";
	
	// Environment properties
	public static final String SELECTED_AUTH = "SELECTED_AUTH";
	public static final String SELECTED_AUTH_DB = "db";
	public static final String SELECTED_AUTH_LDAP = "ldap";
	
	// Headers
	public static final String JWT_TOKEN = "jwt";
	public static final String SET_COOKIE = "Set-Cookie";
	
	// JWT
	public static final long VALIDITY_SECONDS = 900l;
}