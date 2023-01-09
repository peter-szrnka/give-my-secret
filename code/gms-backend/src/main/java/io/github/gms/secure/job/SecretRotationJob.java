package io.github.gms.secure.job;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.SecretRotationService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.secretrotation.enabled", havingValue = "true", matchIfMissing = true)
public class SecretRotationJob {
	
	@Autowired
	private Clock clock;
	
	@Autowired
	private SecretRepository secretRepository;

	@Autowired
	private SecretRotationService service;

	@Scheduled(cron = "0/30 * * * * ?")
	public void execute() {
		List<SecretEntity> resultList = secretRepository.findAllOldRotated(LocalDateTime.now(clock).minusSeconds(55l));
		
		AtomicLong counter = new AtomicLong(0L);
		resultList.forEach(secretEntity -> {
			if (shouldNotRotate(secretEntity)) {
				return;
			}

			service.rotateSecret(secretEntity);
			counter.incrementAndGet();
		});
		
		if (counter.get() > 0) {
			log.info("{} entities updated", counter.get());
		}
	}

	private boolean shouldNotRotate(SecretEntity secretEntity) {
		RotationPeriod rotationPeriod = secretEntity.getRotationPeriod();
		LocalDateTime comparisonDate = LocalDateTime.now(clock).minus(
				rotationPeriod.getUnitValue(), 
				rotationPeriod.getUnit());

		return secretEntity.getLastRotated().isAfter(comparisonDate);
	}
}
