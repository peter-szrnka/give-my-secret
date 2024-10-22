package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.event.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.enums.SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED;
import static io.github.gms.common.enums.SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventMaintenanceJob extends AbstractLimitBasedJob {

    private final EventRepository eventRepository;

    @Override
    @Scheduled(cron = "0 15 * * * ?")
    public void run() {
        execute(this::businessLogic);
    }

    @Override
    protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
        return Pair.of(EVENT_MAINTENANCE_JOB_ENABLED, EVENT_MAINTENANCE_RUNNER_CONTAINER_ID);
    }

    private void businessLogic() {
        int result = eventRepository.deleteAllEventDateOlderThan(processConfig(SystemProperty.JOB_OLD_EVENT_LIMIT));

        if (result > 0) {
            log.info("{} event(s) deleted", result);
        }
    }
}
