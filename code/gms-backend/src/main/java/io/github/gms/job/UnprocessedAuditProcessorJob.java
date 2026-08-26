package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.model.UserEvent;
import io.github.gms.functions.event.EventService;
import io.github.gms.functions.event.UnprocessedEventStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "config.audit.enableDetailed", havingValue = "true")
public class UnprocessedAuditProcessorJob extends AbstractJob {

    private final UnprocessedEventStorage unprocessedEventStorage;
    private final EventService eventService;

    @Override
    @SchedulerLock(name = "unprocessedAuditProcessorJob",
            lockAtLeastFor = "${config.job.unprocessedAuditProcessorJob.lockAtLeastFor}",
            lockAtMostFor = "${config.job.unprocessedAuditProcessorJob.lockAtMostFor}")
    @Scheduled(cron = "30 * * * * ?")
    public void run() {
        execute(this::businessLogic);
    }

    private void businessLogic() {
        List<UserEvent> events = unprocessedEventStorage.getAll(true);
        log.info("Number of unprocessed events: {}", events.size());

        if (events.isEmpty()) {
            return;
        }

        events.forEach(eventService::saveUserEvent);
    }

    @Override
    protected SystemProperty enableConfig() {
        return SystemProperty.UNPROCESSED_AUDIT_LOGS_ENABLED;
    }
}
