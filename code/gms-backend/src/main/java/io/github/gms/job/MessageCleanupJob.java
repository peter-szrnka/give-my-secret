package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.enums.SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED;

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
	@SchedulerLock(name = "messageCleanupJob",
		lockAtLeastFor = "${config.job.messageCleanupJob.lockAtLeastFor}",
		lockAtMostFor = "${config.job.messageCleanupJob.lockAtMostFor}")
	@Scheduled(cron = "0 0 * * * ?")
	public void run() {
		execute(this::execute);
	}

	@Override
	protected SystemProperty enableConfig() {
		return MESSAGE_CLEANUP_JOB_ENABLED;
	}

	private void execute() {
		int result = messageRepository.deleteAllEventDateOlderThan(processConfig(SystemProperty.JOB_OLD_MESSAGE_LIMIT));

		if (result > 0) {
			log.info("{} message(s) deleted", result);
		}
	}
}
