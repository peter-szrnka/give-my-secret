package io.github.gms.secure.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.secure.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.eventmaintenance.enabled", havingValue = "true", matchIfMissing = true)
public class EventMaintenanceJob extends AbstractLimitBasedJob {
	
	@Autowired
	private EventRepository eventRepository;
	
	@Value("${config.event.old.limit}")
	private String oldEventLimit;

	@Scheduled(cron = "15 0 * * * ?")
	public void execute() {
		List<EventEntity> resultList = eventRepository.findAllEventDateOlderThan(processConfig(oldEventLimit));
		eventRepository.deleteAll(resultList);
		
		if (!resultList.isEmpty()) {
			log.info("{} event(s) deleted", resultList.size());
		}
	}
}
