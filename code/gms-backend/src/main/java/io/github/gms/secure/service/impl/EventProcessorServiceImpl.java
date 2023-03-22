package io.github.gms.secure.service.impl;

import io.github.gms.common.event.EntityChangeEvent;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.EventProcessorService;
import io.github.gms.secure.service.MessageService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class EventProcessorServiceImpl implements EventProcessorService {
	
	static final String REASON_PREFIX = "Some of your secrets have been disabled. Reason: ";
	static final String REASON_KEYSTORE_DISABLED = "You disabled a keystore that you used for the given secrets.";
	static final String REASON_KEYSTORE_ALIAS_REMOVED = "You removed a keystore alias that is used for a given secret.";

	private final MessageService messageService;
	private final SecretRepository secretRepository;
	private final KeystoreAliasRepository keystoreAliasRepository;

	public EventProcessorServiceImpl(MessageService messageService, SecretRepository secretRepository,
									 KeystoreAliasRepository keystoreAliasRepository) {
		this.messageService = messageService;
		this.secretRepository = secretRepository;
		this.keystoreAliasRepository = keystoreAliasRepository;
	}

	@Override
	@Transactional
	@EventListener
	public void disableEntity(EntityChangeEvent event) {
		Long userId = (Long) event.getMetadata().get("userId");
		Long keystoreId = (Long) event.getMetadata().get("keystoreId");

		if (event.getType() == EntityChangeEvent.EntityChangeType.KEYSTORE_DISABLED) {
			// We have to disable all secret by alias id where the given keystore is used
			keystoreAliasRepository.findAllByKeystoreId(keystoreId)
				.forEach(alias -> secretRepository.disableAllActiveByKeystoreAliasId(alias.getId()));
			sendMessage(userId, REASON_KEYSTORE_DISABLED);
			return;
		}
		
		Long aliasId = (Long) event.getMetadata().get("aliasId");
		secretRepository.disableAllActiveByKeystoreAliasId(aliasId);

		sendMessage(userId, REASON_KEYSTORE_ALIAS_REMOVED);
	}
	
	private void sendMessage(Long userId, String reason) {
		messageService.save(MessageDto.builder()
				.userId(userId).message(REASON_PREFIX + reason).build());
	}
}
