package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.UserAssetDeletionService;
import io.github.gms.functions.maintenance.UserDeletionService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
public class UserDeletionJob extends AbstractJob {

    private final UserDeletionService userDeletionService;
    private final UserAssetDeletionService userAssetDeletionService;

    public UserDeletionJob(SystemService systemService, SystemPropertyService systemPropertyService, UserDeletionService userDeletionService, UserAssetDeletionService userAssetDeletionService) {
        super(systemService, systemPropertyService, SystemProperty.USER_DELETION_JOB_ENABLED);
        this.userDeletionService = userDeletionService;
        this.userAssetDeletionService = userAssetDeletionService;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        if (skipJobExecution(SystemProperty.USER_DELETION_RUNNER_CONTAINER_ID)) {
            return;
        }

        Set<Long> userIds = userDeletionService.getRequestedUserIds();

        if (userIds.isEmpty()) {
            return;
        }

        log.info("{} user(s) requested to delete", userIds.size());

        log.info("Deleting requested user assets(API keys, secrets, keystore resources,etc.)");
        userAssetDeletionService.executeRequestedUserAssetDeletion(userIds);

        log.info("Deleting requested users");
        userDeletionService.process(userIds);
    }
}
