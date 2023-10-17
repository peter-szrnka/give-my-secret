package io.github.gms.auth.ldap;

import io.github.gms.auth.model.GmsUserDetails;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface LdapUserPersistenceService {
    
    GmsUserDetails saveUserIfRequired(String username, GmsUserDetails foundUser);
}