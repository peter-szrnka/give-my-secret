package io.github.gms.secure.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.secure.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.messagecleanup.enabled", havingValue = "true", matchIfMissing = true)
public class MessageCleanupJob extends AbstractLimitBasedJob {
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Value("${config.message.old.limit}")
	private String oldMessageLimit;
	
	@Scheduled(cron = "0 0 * * * ?")
	public void execute() {
		List<MessageEntity> resultList = messageRepository.findAllEventDateOlderThan(processConfig(oldMessageLimit));
		messageRepository.deleteAll(resultList);
		
		if (!resultList.isEmpty()) {
			log.info("{} message(s) deleted", resultList.size());
		}
	}
}
