package io.github.gms.secure.job;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.gms.common.enums.TimeUnit;
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
public class EventMaintenanceJob {
	
	@Autowired
	private Clock clock;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Value("${config.event.old.limit}")
	private String oldEventLimit;

	@Scheduled(cron = "0 0 * * * ?")
	public void execute() {
		List<EventEntity> resultList = eventRepository.findAllEventDateOlderThan(processConfig(oldEventLimit));
		eventRepository.deleteAll(resultList);
		
		if (!resultList.isEmpty()) {
			log.info("{} event(s) deleted", resultList.size());
		}
	}

	private LocalDateTime processConfig(String oldEventLimitValue) {
		String[] values = oldEventLimitValue.split(";");
		TimeUnit timeUnit = TimeUnit.getByCode(values[1]);
		return LocalDateTime.now(clock).minus(Long.parseLong(values[0]), timeUnit.getUnit());
	}
}
