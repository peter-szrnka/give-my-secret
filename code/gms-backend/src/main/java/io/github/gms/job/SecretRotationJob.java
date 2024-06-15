package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.secret.SecretRotationService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.gms.common.util.Constants.TRUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.secretRotation.enabled", havingValue = TRUE, matchIfMissing = true)
public class SecretRotationJob extends AbstractJob {
	
	private static final long DELAY_SECONDS = 55L;
	private final Clock clock;
	private final SecretRepository secretRepository;
	private final SecretRotationService service;

	public SecretRotationJob(
			Environment environment,
			SystemPropertyService systemPropertyService,
			Clock clock,
			SecretRepository secretRepository,
			SecretRotationService service) {
		super(environment, systemPropertyService);
		this.clock = clock;
		this.secretRepository = secretRepository;
		this.service = service;
	}

	@Scheduled(cron = "0 * * * * ?")
	public void execute() {
		if (skipJobExecution(SystemProperty.SECRET_ROTATION_RUNNER_CONTAINER_ID)) {
			return;
		}

		List<SecretEntity> resultList = secretRepository.findAllOldRotated(ZonedDateTime.now(clock).minusSeconds(DELAY_SECONDS));
		
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
		ZonedDateTime comparisonDate = ZonedDateTime.now(clock).minus(
				rotationPeriod.getUnitValue(), 
				rotationPeriod.getUnit());

		return secretEntity.getLastRotated().isAfter(comparisonDate);
	}
}
