package io.github.gms.functions.user;

import io.github.gms.auth.ldap.LdapSyncService;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.types.Audited;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;
import static io.github.gms.common.util.Constants.ROLE_ADMIN;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/user")
@Profile(value = { CONFIG_AUTH_TYPE_LDAP })
public class LdapUserController {

    private final LdapSyncService ldapSyncService;
    private final String authType;

    public LdapUserController(
            LdapSyncService ldapSyncService,
            @Value("${config.auth.type}") String authType) {
        this.ldapSyncService = ldapSyncService;
        this.authType = authType;
    }

    @GetMapping("/sync_ldap_users")
    @PreAuthorize(ROLE_ADMIN)
    @Audited(operation = EventOperation.SYNC_LDAP_USERS_MANUALLY)
    public ResponseEntity<Void> synchronizeUsers() {
        if (!SELECTED_AUTH_LDAP.equals(authType)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ldapSyncService.synchronizeUsers();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
