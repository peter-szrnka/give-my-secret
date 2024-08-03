package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.event.EventRepository;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;

import static io.github.gms.common.util.Constants.TRUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.eventMaintenance.enabled", havingValue = TRUE, matchIfMissing = true)
public class EventMaintenanceJob extends AbstractLimitBasedJob {

	private final EventRepository eventRepository;

	public EventMaintenanceJob(SystemService systemService, Clock clock, EventRepository eventRepository, SystemPropertyService systemPropertyService) {
		super(systemService, clock, systemPropertyService);
		this.eventRepository = eventRepository;
	}

	@Scheduled(cron = "0 15 * * * ?")
	public void execute() {
		if (skipJobExecution(SystemProperty.EVENT_MAINTENANCE_RUNNER_CONTAINER_ID)) {
			return;
		}

		int result = eventRepository.deleteAllEventDateOlderThan(processConfig(SystemProperty.JOB_OLD_EVENT_LIMIT));

		if (result > 0) {
			log.info("{} event(s) deleted", result);
		}
	}
}
