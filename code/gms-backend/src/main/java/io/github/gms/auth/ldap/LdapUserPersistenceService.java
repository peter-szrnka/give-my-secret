package io.github.gms.auth.ldap;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface LdapUserPersistenceService {

    void synchronizeUsers();

    
    //GmsUserDetails saveUserIfRequired(String username, GmsUserDetails foundUser);
}