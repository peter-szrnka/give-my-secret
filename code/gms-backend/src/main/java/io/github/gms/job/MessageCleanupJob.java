package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.functions.message.MessageRepository;
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
@ConditionalOnProperty(value = "config.job.messageCleanup.enabled", havingValue = "true", matchIfMissing = true)
public class MessageCleanupJob extends AbstractLimitBasedJob {

	private final MessageRepository messageRepository;
	private final String oldMessageLimit;

	public MessageCleanupJob(Clock clock, MessageRepository messageRepository, @Value("${config.message.old.limit}") String oldMessageLimit) {
		super(clock);
		this.messageRepository = messageRepository;
		this.oldMessageLimit = oldMessageLimit;
	}
	
	@Scheduled(cron = "0 0 * * * ?")
	public void execute() {
		int resultList = messageRepository.deleteAllEventDateOlderThan(processConfig(oldMessageLimit));

		if (resultList > 0) {
			log.info("{} message(s) deleted", resultList);
		}
	}
}
