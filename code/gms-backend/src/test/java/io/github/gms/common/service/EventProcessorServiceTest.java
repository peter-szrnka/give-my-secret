package io.github.gms.common.service;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.model.EntityChangeEvent;
import io.github.gms.common.model.EntityChangeEvent.EntityChangeType;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.message.MessageDto;
import io.github.gms.functions.message.MessageService;
import io.github.gms.functions.secret.SecretRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventProcessorServiceTest extends AbstractUnitTest {

	private MessageService messageService;
	private SecretRepository secretRepository;
	private KeystoreAliasRepository keystoreAliasRepository;
	private EventProcessorService service;

	@BeforeEach
	public void setup() {
		messageService = mock(MessageService.class);
		secretRepository = mock(SecretRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
		service = new EventProcessorService(messageService, secretRepository, keystoreAliasRepository);
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
		metadata.put("userId", 5L);
		metadata.put("keystoreId", 6L);
		metadata.put("aliasId", 2L);
		service.disableEntity(new EntityChangeEvent(new Object(), metadata, input.eventType));
		
		// assert
		ArgumentCaptor<Long> keystoreIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<MessageDto> messageDtoCaptor = ArgumentCaptor.forClass(MessageDto.class);
		verify(secretRepository).disableAllActiveByKeystoreAliasId(keystoreIdCaptor.capture());
		verify(keystoreAliasRepository, times(input.eventType == EntityChangeType.KEYSTORE_DISABLED ? 1 : 0)).findAllByKeystoreId(anyLong());
		verify(messageService).save(messageDtoCaptor.capture());
		
		assertEquals(input.eventType == EntityChangeType.KEYSTORE_DISABLED ? 1L : 2L, keystoreIdCaptor.getValue());
		
		MessageDto capturedDto = messageDtoCaptor.getValue();
		assertNotNull(capturedDto);
		assertEquals(5L, capturedDto.getUserId());
		assertEquals(input.expectedMessage,
				capturedDto.getMessage());
	}
	
	private static InputData[] input() {
		return new InputData[] {
			new InputData(EntityChangeType.KEYSTORE_DISABLED, 
					EventProcessorService.REASON_PREFIX + EventProcessorService.REASON_KEYSTORE_DISABLED),
			new InputData(EntityChangeType.KEYSTORE_ALIAS_REMOVED, 
					EventProcessorService.REASON_PREFIX + EventProcessorService.REASON_KEYSTORE_ALIAS_REMOVED)
		};
	}

	@ToString
	@AllArgsConstructor
	private static class InputData {
		private EntityChangeEvent.EntityChangeType eventType;
		private String expectedMessage;
	}
}
