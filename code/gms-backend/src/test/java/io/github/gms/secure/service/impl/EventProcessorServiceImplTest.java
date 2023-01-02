package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.event.EntityDisabledEvent;
import io.github.gms.common.event.EntityDisabledEvent.EntityType;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.MessageService;

/**
 * Unit test of {@link EventProcessorServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EventProcessorServiceImplTest extends AbstractUnitTest {
	
	@Mock
	private MessageService messageService;
	@Mock
	private SecretRepository secretRepository;
	@InjectMocks
	private EventProcessorServiceImpl service;
	
	@Test
	void shouldSkipDisableProcess() {
		// act
		service.disableEntity(new EntityDisabledEvent(new Object(), 1L, 1L, EntityType.API_KEY));
		
		// assert
		verify(secretRepository, never()).disableAllByKeystoreId(anyLong());
		verify(messageService, never()).save(any(MessageDto.class));
	}
	
	@Test
	void shouldDisableEntity() {
		// act
		service.disableEntity(new EntityDisabledEvent(new Object(), 1L, 1L, EntityType.KEYSTORE));
		
		// assert
		ArgumentCaptor<Long> keystoreIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<MessageDto> messageDtoCaptor = ArgumentCaptor.forClass(MessageDto.class);
		verify(secretRepository).disableAllByKeystoreId(keystoreIdCaptor.capture());
		verify(messageService).save(messageDtoCaptor.capture());
		
		assertEquals(1L, keystoreIdCaptor.getValue());
		
		MessageDto capturedDto = messageDtoCaptor.getValue();
		assertNotNull(capturedDto);
		assertEquals("Your keystore entity(ID=1) has been disabled. Reason: You disabled the keystore earlier that you used for the given Secret.",
				capturedDto.getMessage());
	}
}