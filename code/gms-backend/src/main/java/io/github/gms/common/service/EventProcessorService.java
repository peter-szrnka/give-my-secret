package io.github.gms.common.service;

import io.github.gms.common.model.EntityChangeEvent;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.message.MessageDto;
import io.github.gms.functions.message.MessageService;
import io.github.gms.functions.secret.SecretRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.github.gms.common.util.Constants.ALIAS_ID;
import static io.github.gms.common.util.Constants.KEYSTORE_ID;
import static io.github.gms.common.util.Constants.USER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class EventProcessorService {

	public static final String REASON_PREFIX = "Some of your secrets have been disabled. Reason: ";
	public static final String REASON_KEYSTORE_DISABLED = "You disabled a keystore that you used for the given secrets.";
	public static final String REASON_KEYSTORE_ALIAS_REMOVED = "You removed a keystore alias that is used for a given secret.";

	private final MessageService messageService;
	private final SecretRepository secretRepository;
	private final KeystoreAliasRepository keystoreAliasRepository;

	@Transactional
	@EventListener
	public void disableEntity(EntityChangeEvent event) {
		Long userId = (Long) event.getMetadata().get(USER_ID);
		Long keystoreId = (Long) event.getMetadata().get(KEYSTORE_ID);

		if (event.getType() == EntityChangeEvent.EntityChangeType.KEYSTORE_DISABLED) {
			// We have to disable all secret by alias id where the given keystore is used
			keystoreAliasRepository.findAllByKeystoreId(keystoreId)
					.forEach(alias -> secretRepository.disableAllActiveByKeystoreAliasId(alias.getId()));
			sendMessage(userId, REASON_KEYSTORE_DISABLED);
			return;
		}

		Long aliasId = (Long) event.getMetadata().get(ALIAS_ID);
		secretRepository.disableAllActiveByKeystoreAliasId(aliasId);

		sendMessage(userId, REASON_KEYSTORE_ALIAS_REMOVED);
	}

	private void sendMessage(Long userId, String reason) {
		messageService.save(MessageDto.builder()
				.userId(userId).message(REASON_PREFIX + reason).build());
	}
}
