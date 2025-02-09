package io.github.gms.functions.keystore;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.common.service.FileService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreDataServiceTest extends AbstractLoggingUnitTest {

	private KeystoreRepository keystoreRepository;
	private KeystoreAliasRepository keystoreAliasRepository;
	private KeystoreDataService service;
	private FileService fileService;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		keystoreRepository = mock(KeystoreRepository.class);
		keystoreAliasRepository = mock(KeystoreAliasRepository.class);
		fileService = mock(FileService.class);
		service = new KeystoreDataService(keystoreRepository, keystoreAliasRepository, fileService, "src/test/resources/");
	}
	
	@Test
	void getKeystoreData_whenKeystoreAliasMissing_thenThrowException() {
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
	void getKeystoreData_whenKeystoreEntityMissing_thenThrowException() {
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
	void getKeystoreData_whenAllConditionsMet_thenReturnKeystorePair() throws IOException, CertificateException, NoSuchAlgorithmException {
		try (MockedStatic<KeyStore> keyStoreMockedStatic = mockStatic(KeyStore.class)) {
			// arrange
			SecretEntity entity = TestUtils.createSecretEntity();
			when(keystoreAliasRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
			when(keystoreRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createJKSKeystoreEntity()));
			when(fileService.toByteArray(any(File.class))).thenReturn("data".getBytes());
			KeyStore mockKeyStore = mock(KeyStore.class);
			keyStoreMockedStatic.when(() -> KeyStore.getInstance(anyString())).thenReturn(mockKeyStore);

			// act
			KeystorePair response = assertDoesNotThrow(() -> service.getKeystoreData(entity));

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
