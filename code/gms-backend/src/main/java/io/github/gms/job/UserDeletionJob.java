package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.gdpr.UserAssetDeletionService;
import io.github.gms.functions.gdpr.UserDeletionService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
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
@ConditionalOnProperty(value = "config.job.userDeletion.enabled", havingValue = TRUE, matchIfMissing = true)
public class UserDeletionJob extends AbstractJob {

    private final UserDeletionService userDeletionService;
    private final UserAssetDeletionService userAssetDeletionService;

    public UserDeletionJob(SystemService systemService, SystemPropertyService systemPropertyService, UserDeletionService userDeletionService, UserAssetDeletionService userAssetDeletionService) {
        super(systemService, systemPropertyService);
        this.userDeletionService = userDeletionService;
        this.userAssetDeletionService = userAssetDeletionService;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        if (skipJobExecution(SystemProperty.USER_DELETION_RUNNER_CONTAINER_ID)) {
            return;
        }

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
