package io.github.gms.auth.ldap;

import org.springframework.data.util.Pair;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface LdapSyncService {

    Pair<Integer, Integer> synchronizeUsers();
}