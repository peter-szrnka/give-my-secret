package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.UserEvent;
import io.github.gms.functions.event.UnprocessedEventStorage;
import io.github.gms.functions.event.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnprocessedAuditProcessorJob extends AbstractJob {

    private final UnprocessedEventStorage unprocessedEventStorage;
    private final EventService eventService;

    @Override
    @Scheduled(cron = "30 * * * * ?")
    public void run() {
        execute(this::businessLogic);
    }

    private void businessLogic() {
        List<UserEvent> events = unprocessedEventStorage.getAll(true);
        log.info("Number of queue events: {}", events.size());

        if (events.isEmpty()) {
            return;
        }

        events.forEach(eventService::saveUserEvent);
    }

    @Override
    protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
        return Pair.of(SystemProperty.UNPROCESSED_AUDIT_LOGS_ENABLED, SystemProperty.UNPROCESSED_AUDIT_LOGS_RUNNER_CONTAINER_ID);
    }
}
