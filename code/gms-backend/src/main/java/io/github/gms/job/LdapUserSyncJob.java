package io.github.gms.job;

import io.github.gms.auth.ldap.LdapSyncService;
import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

@Slf4j
@Component
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class LdapUserSyncJob extends AbstractJob {

    private final LdapSyncService service;

    public LdapUserSyncJob(SystemService systemService, SystemPropertyService systemPropertyService, LdapSyncService service) {
        super(systemService, systemPropertyService, SystemProperty.LDAP_SYNC_JOB_ENABLED);
        this.service = service;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void execute() {
        if (skipJobExecution(SystemProperty.LDAP_SYNC_RUNNER_CONTAINER_ID)) {
            return;
        }

        Pair<Integer, Integer> result = service.synchronizeUsers();
        log.info("{} user(s) synchronized and {} of them {} been marked as deletable.",
                result.getFirst(), result.getSecond(), result.getSecond() > 1 ? "have" : "has");
    }
}
