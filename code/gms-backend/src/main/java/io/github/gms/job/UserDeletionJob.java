package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.user.UserAssetDeletionService;
import io.github.gms.functions.maintenance.user.UserDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionJob extends AbstractJob {

    private final UserDeletionService userDeletionService;
    private final UserAssetDeletionService userAssetDeletionService;

    @Override
    @SchedulerLock(name = "userDeletionJob",
            lockAtLeastFor = "${config.job.userDeletionJob.lockAtLeastFor}",
            lockAtMostFor = "${config.job.userDeletionJob.lockAtMostFor}")
    @Scheduled(cron = "0 */5 * * * ?")
    public void run() {
        execute(this::businessLogic);
    }

    @Override
    protected SystemProperty enableConfig() {
        return SystemProperty.USER_DELETION_JOB_ENABLED;
    }

    private void businessLogic() {
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
