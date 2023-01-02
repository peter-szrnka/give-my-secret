package io.github.gms.secure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gms.common.event.EntityDisabledEvent;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.EventProcessorService;
import io.github.gms.secure.service.MessageService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class EventProcessorServiceImpl implements EventProcessorService {

	@Autowired
	private MessageService messageService;
	@Autowired
	private SecretRepository secretRepository;

	@Override
	@Transactional
	@EventListener
	public void disableEntity(EntityDisabledEvent event) {
		if (event.getType() != EntityDisabledEvent.EntityType.KEYSTORE) {
			return;
		}
		
		secretRepository.disableAllByKeystoreId(event.getId());
		String reason = "You disabled the keystore earlier that you used for the given Secret.";

		messageService.save(MessageDto.builder()
				.userId(event.getUserId()).message("Your " + event.getType().getDisplayName()
				+ " entity(ID=" + event.getId() + ") has been disabled. Reason: " + reason).build());
	}
}
