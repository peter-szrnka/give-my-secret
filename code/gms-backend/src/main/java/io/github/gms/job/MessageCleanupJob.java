package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.enums.SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED;
import static io.github.gms.common.enums.SystemProperty.MESSAGE_CLEANUP_RUNNER_CONTAINER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageCleanupJob extends AbstractLimitBasedJob {

	private final MessageRepository messageRepository;

	@Override
	@Scheduled(cron = "0 0 * * * ?")
	public void run() {
		execute(this::execute);
	}

	@Override
	protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
		return Pair.of(MESSAGE_CLEANUP_JOB_ENABLED, MESSAGE_CLEANUP_RUNNER_CONTAINER_ID);
	}

	private void execute() {
		int result = messageRepository.deleteAllEventDateOlderThan(processConfig(SystemProperty.JOB_OLD_MESSAGE_LIMIT));

		if (result > 0) {
			log.info("{} message(s) deleted", result);
		}
	}
}
