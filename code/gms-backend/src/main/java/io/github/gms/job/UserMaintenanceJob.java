package io.github.gms.job;

import io.github.gms.functions.gdpr.UserAssetDeletionService;
import io.github.gms.functions.gdpr.UserDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static io.github.gms.common.util.Constants.TRUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "config.job.userMaintenance.enabled", havingValue = TRUE, matchIfMissing = true)
public class UserMaintenanceJob {

    private final UserDeletionService userDeletionService;
    private final UserAssetDeletionService userAssetDeletionService;

    @Scheduled(cron = "0 5 * * * ?")
    public void execute() {
        Set<Long> userIds = userDeletionService.getRequestedUserDeletionIds();

        if (userIds.isEmpty()) {
            return;
        }

        log.info("{} user(s) requested to delete", userIds.size());

        log.info("Deleting requested user assets(API keys, secrets, keystore resources,etc.)");
        userAssetDeletionService.executeRequestedUserAssetDeletion(userIds);

        log.info("Deleting requested users");
        userDeletionService.executeRequestedUserDeletion(userIds);
    }
}
