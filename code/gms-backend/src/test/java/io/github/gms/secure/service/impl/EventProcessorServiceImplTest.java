package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.event.EntityChangeEvent;
import io.github.gms.common.event.EntityChangeEvent.EntityChangeType;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.MessageService;
import io.github.gms.util.TestUtils;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link EventProcessorServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EventProcessorServiceImplTest extends AbstractUnitTest {

	private MessageService messageService;
	private SecretRepository secretRepository;
	private KeystoreAliasRepository keystoreAliasRepository;
	private EventProcessorServiceImpl service;

	@BeforeEach
	public void setup() {
		messageService = mock(MessageService.class);
		secretRepository = mock(SecretRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
		service = new EventProcessorServiceImpl(messageService, secretRepository, keystoreAliasRepository);
	}
	
	@ParameterizedTest
	@MethodSource("input")
	void shouldDisableKeystoreEntity(InputData input) {
		// arrange
		if (input.eventType == EntityChangeType.KEYSTORE_DISABLED) {
			when(keystoreAliasRepository.findAllByKeystoreId(anyLong())).thenReturn(List.of(TestUtils.createKeystoreAliasEntity()));
		}
		
		// act
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("userId", 1L);
		metadata.put("keystoreId", 1L);
		service.disableEntity(new EntityChangeEvent(new Object(), metadata, input.eventType));
		
		// assert
		ArgumentCaptor<Long> keystoreIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<MessageDto> messageDtoCaptor = ArgumentCaptor.forClass(MessageDto.class);
		verify(secretRepository).disableAllActiveByKeystoreAliasId(keystoreIdCaptor.capture());
		verify(keystoreAliasRepository, times(input.eventType == EntityChangeType.KEYSTORE_DISABLED ? 1 : 0)).findAllByKeystoreId(anyLong());
		verify(secretRepository, times(input.eventType == EntityChangeType.KEYSTORE_DISABLED ? 1 : 0)).disableAllActiveByKeystoreAliasId(anyLong());
		verify(messageService).save(messageDtoCaptor.capture());
		
		assertEquals(input.eventType == EntityChangeType.KEYSTORE_DISABLED ? 1L : null, keystoreIdCaptor.getValue());
		
		MessageDto capturedDto = messageDtoCaptor.getValue();
		assertNotNull(capturedDto);
		assertEquals(input.expectedMessage,
				capturedDto.getMessage());
	}
	
	private static InputData[] input() {
		return new InputData[] {
			new InputData(EntityChangeType.KEYSTORE_DISABLED, 
					EventProcessorServiceImpl.REASON_PREFIX + EventProcessorServiceImpl.REASON_KEYSTORE_DISABLED),
			new InputData(EntityChangeType.KEYSTORE_ALIAS_REMOVED, 
					EventProcessorServiceImpl.REASON_PREFIX + EventProcessorServiceImpl.REASON_KEYSTORE_ALIAS_REMOVED)
		};
	}

	@ToString
	@AllArgsConstructor
	private static class InputData {
		private EntityChangeEvent.EntityChangeType eventType;
		private String expectedMessage;
	}
}
