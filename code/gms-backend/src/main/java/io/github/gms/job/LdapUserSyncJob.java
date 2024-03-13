package io.github.gms.job;

import io.github.gms.auth.ldap.LdapSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "config.job.ldapUserSyncJob.enabled", havingValue = "true")
public class LdapUserSyncJob {

    private final LdapSyncService service;

    @Scheduled(cron = "0 /10 * * * ?")
    public void execute() {
        int result = service.synchronizeUsers();
        log.info("{} users(s) synchronized", result);
    }
}
