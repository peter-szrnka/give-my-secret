package io.github.gms.job;

import io.github.gms.auth.ldap.LdapSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
@ConditionalOnProperty(value = "config.job.ldapUserSyncJob.enabled", havingValue = "true")
public class LdapUserSyncJob {

    private final LdapSyncService service;

    @Scheduled(cron = "0 */10 * * * ?")
    public void execute() {
        Pair<Integer, Integer> result = service.synchronizeUsers();
        log.info("{} user(s) synchronized and {} of them {} been marked as deletable.",
                result.getFirst(), result.getSecond(), result.getSecond() > 1 ? "have" : "has");
    }
}
