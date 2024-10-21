package io.github.gms.job;

import io.github.gms.auth.ldap.LdapSyncService;
import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_LDAP)
public class LdapUserSyncJob extends AbstractJob {

    private final LdapSyncService service;

    @Override
    @Scheduled(cron = "0 */10 * * * ?")
    public void run() {
        execute(this::businessLogic);
    }

    @Override
    protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
        return Pair.of(SystemProperty.LDAP_SYNC_JOB_ENABLED, SystemProperty.LDAP_SYNC_RUNNER_CONTAINER_ID);
    }

    private void businessLogic() {
        Pair<Integer, Integer> result = service.synchronizeUsers();
        log.info("{} user(s) synchronized and {} of them {} been marked as deletable.",
                result.getFirst(), result.getSecond(), result.getSecond() > 1 ? "have" : "has");
    }
}
