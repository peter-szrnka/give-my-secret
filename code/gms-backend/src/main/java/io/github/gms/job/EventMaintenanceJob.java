package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.functions.event.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.eventMaintenance.enabled", havingValue = "true", matchIfMissing = true)
public class EventMaintenanceJob extends AbstractLimitBasedJob {

	private final EventRepository eventRepository;
	private final String oldEventLimit;

	public EventMaintenanceJob(Clock clock, EventRepository eventRepository, @Value("${config.event.old.limit}") String oldEventLimit) {
		super(clock);
		this.eventRepository = eventRepository;
		this.oldEventLimit = oldEventLimit;
	}

	@Scheduled(cron = "0 15 * * * ?")
	public void execute() {
		int result = eventRepository.deleteAllEventDateOlderThan(processConfig(oldEventLimit));

		if (result > 0) {
			log.info("{} event(s) deleted", result);
		}
	}
}
