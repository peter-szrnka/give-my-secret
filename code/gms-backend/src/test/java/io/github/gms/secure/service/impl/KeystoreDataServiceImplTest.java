package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import java.security.KeyStore;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreDataServiceImplTest extends AbstractLoggingUnitTest {

	private KeystoreRepository keystoreRepository;
	private KeystoreAliasRepository keystoreAliasRepository;
	private KeystoreDataServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		keystoreRepository = mock(KeystoreRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
		service = new KeystoreDataServiceImpl(keystoreRepository, keystoreAliasRepository, "src/test/resources/");
	}
	
	@Test
	@SneakyThrows
	void shouldThrowExceptionWhenKeystoreAliasMissing() {
		// arrange
		SecretEntity entity = TestUtils.createSecretEntityWithUniqueKeystoreAliasId(1L);
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getKeystoreData(entity));

		// assert
		assertEquals("Invalid keystore alias!", exception.getMessage());
		verify(keystoreAliasRepository).findById(anyLong());
	}
	
	@Test
	@SneakyThrows
	void shouldNotGetKeystoreData() {
		// arrange
		SecretEntity entity = TestUtils.createSecretEntity();
		when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		when(keystoreRepository.findById(anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getKeystoreData(entity));
	    
	    // assert
	    assertEquals("Keystore entity not found!", exception.getMessage());
		verify(keystoreAliasRepository).findById(anyLong());
		verify(keystoreRepository).findById(anyLong());
	}
	
	@Test
	@SneakyThrows
	void shouldGetKeystoreData() {
		try (MockedStatic<KeyStore> keyStoreMockedStatic = mockStatic(KeyStore.class)) {
			// arrange
			SecretEntity entity = TestUtils.createSecretEntity();
			when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
			when(keystoreRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createJKSKeystoreEntity()));
			KeyStore mockKeyStore = mock(KeyStore.class);
			keyStoreMockedStatic.when(() -> KeyStore.getInstance(anyString())).thenReturn(mockKeyStore);

			// act
			KeystorePair response = service.getKeystoreData(entity);

			// assert
			assertNotNull(response);
			assertNotNull(response.getEntity());
			assertNotNull(response.getKeystore());
			verify(keystoreAliasRepository).findById(anyLong());
			verify(keystoreRepository).findById(anyLong());
			verify(mockKeyStore).load(any(), ArgumentMatchers.eq("test".toCharArray()));
			keyStoreMockedStatic.verify(() -> KeyStore.getInstance(anyString()));
		}
	}
}
