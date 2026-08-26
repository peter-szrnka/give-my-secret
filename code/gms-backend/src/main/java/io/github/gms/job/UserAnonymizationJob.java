package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.user.UserAnonymizationService;
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
public class UserAnonymizationJob extends AbstractJob {

    private final UserAnonymizationService userAnonymizationService;

    @Override
    @SchedulerLock(name = "userAnonymizationJob",
            lockAtLeastFor = "${config.job.userAnonymizationJob.lockAtLeastFor}",
            lockAtMostFor = "${config.job.userAnonymizationJob.lockAtMostFor}")
    @Scheduled(cron = "0 */5 * * * ?")
    public void run() {
       execute(this::businessLogic);
    }

    @Override
    protected SystemProperty enableConfig() {
        return SystemProperty.USER_ANONYMIZATION_JOB_ENABLED;
    }

    private void businessLogic() {
        Set<Long> userIds = userAnonymizationService.getRequestedUserIds();

        if (userIds.isEmpty()) {
            return;
        }

        log.info("Anonymizing {} requested users", userIds.size());
        userAnonymizationService.process(userIds);
    }
}
