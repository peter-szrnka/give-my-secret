package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.user.UserAnonymizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
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
    @Scheduled(cron = "0 */5 * * * ?")
    public void run() {
       execute(this::businessLogic);
    }

    @Override
    protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
        return Pair.of(SystemProperty.USER_ANONYMIZATION_JOB_ENABLED, SystemProperty.USER_ANONYMIZATION_RUNNER_CONTAINER_ID);
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
