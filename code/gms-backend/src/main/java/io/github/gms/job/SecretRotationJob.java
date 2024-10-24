package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.secret.SecretRotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecretRotationJob extends AbstractJob {
	
	private static final long DELAY_SECONDS = 55L;
	private final SecretRepository secretRepository;
	private final SecretRotationService service;

	@Override
	@Scheduled(cron = "0 * * * * ?")
	public void run() {
		execute(this::businessLogic);
	}

	@Override
	protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
		return Pair.of(SystemProperty.SECRET_ROTATION_JOB_ENABLED, SystemProperty.SECRET_ROTATION_RUNNER_CONTAINER_ID);
	}

	private void businessLogic() {
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
