package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
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
@ConditionalOnProperty(value = "config.job.messageCleanup.enabled", havingValue = "true", matchIfMissing = true)
public class MessageCleanupJob extends AbstractLimitBasedJob {

	private final MessageRepository messageRepository;

	public MessageCleanupJob(Clock clock, MessageRepository messageRepository, SystemPropertyService systemPropertyService) {
		super(clock, systemPropertyService);
		this.messageRepository = messageRepository;
	}
	
	@Scheduled(cron = "0 0 * * * ?")
	public void execute() {
		int result = messageRepository.deleteAllEventDateOlderThan(processConfig(SystemProperty.JOB_OLD_MESSAGE_LIMIT));

		if (result > 0) {
			log.info("{} message(s) deleted", result);
		}
	}
}
