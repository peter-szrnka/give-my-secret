package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.UserAnonymizationService;
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
public class UserAnonymizationJob extends AbstractJob {

    private final UserAnonymizationService userAnonymizationService;

    public UserAnonymizationJob(
            SystemService systemService,
            SystemPropertyService systemPropertyService,
            UserAnonymizationService userAnonymizationService) {
        super(systemService, systemPropertyService, SystemProperty.USER_ANONYMIZATION_JOB_ENABLED);
        this.userAnonymizationService = userAnonymizationService;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        if (skipJobExecution(SystemProperty.USER_ANONYMIZATION_RUNNER_CONTAINER_ID)) {
            return;
        }

        Set<Long> userIds = userAnonymizationService.getRequestedUserIds();

        if (userIds.isEmpty()) {
            return;
        }

        log.info("Anonymizing {} requested users", userIds.size());
        userAnonymizationService.process(userIds);
    }
}
