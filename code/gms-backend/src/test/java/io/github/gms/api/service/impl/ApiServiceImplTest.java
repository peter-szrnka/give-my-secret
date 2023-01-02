package io.github.gms.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.entity.ApiKeyEntity;
import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.entity.UserEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.GetSecretRequestDto;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link ApiServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiServiceImplTest extends AbstractUnitTest {
	
	private static GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

	@Mock
	private CryptoService cryptoService;
	
	@Mock
	private SecretRepository secretRepository;
	
	@Mock
	private ApiKeyRepository apiKeyRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private KeystoreRepository keystoreRepository;
	
	@Mock
	private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
	
	@InjectMocks
	private ApiServiceImpl service;
	
	@Test
	void shouldApiKeyMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(null);

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Wrong API key!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository, never()).findById(anyLong());
	}
	
	@Test
	void shouldUserMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("User not found!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
	}
	
	@Test
	void shouldSecretMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(null);

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Secret is not available!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
	}
	
	@Test
	void shouldKeystoreMissing() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret(false));
		when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.empty());

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("Secret is not available!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
	}
	
	@Test
	void shouldFailBecauseOfApiKeyRestriction() {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret(false));
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
				TestUtils.createApiKeyRestrictionEntity(2L),
				TestUtils.createApiKeyRestrictionEntity(3L)
				));

		// assert
		GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecret(dto));
		assertEquals("You are not allowed to use this API key for this secret!", exception.getMessage());
		
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
	}
	
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldReturnEncrypted(boolean returnDecrypted) {
		// arrange
		when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
		when(userRepository.findById(anyLong())).thenReturn(createMockUser());
		when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(createMockSecret(returnDecrypted));
		when(keystoreRepository.findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
				TestUtils.createApiKeyRestrictionEntity(1L),
				TestUtils.createApiKeyRestrictionEntity(2L),
				TestUtils.createApiKeyRestrictionEntity(3L)
				));
		
		if (returnDecrypted) {
			when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn("decrypted");
		}

		// act
		String response = service.getSecret(dto);
		
		// assert
		Assertions.assertNotNull(response);
		Assertions.assertEquals(returnDecrypted ? "decrypted" : "encrypted", response);
		verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
		verify(userRepository).findById(anyLong());
		verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
		verify(cryptoService, returnDecrypted ? times(1) : never()).decrypt(any(SecretEntity.class));
		verify(keystoreRepository).findByIdAndUserIdAndStatus(anyLong(), anyLong(), eq(EntityStatus.ACTIVE));
	}
	
	private static SecretEntity createMockSecret(boolean returnDecrypted) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setValue("encrypted");
		entity.setReturnDecrypted(returnDecrypted);
		entity.setKeystoreId(1L);
		entity.setUserId(1L);
		return entity;
	}

	private static Optional<UserEntity> createMockUser() {
		UserEntity entity = new UserEntity();
		return Optional.of(entity);
	}

	private static ApiKeyEntity createApiKeyEntity() {
		ApiKeyEntity mockApiKey = new ApiKeyEntity();
		mockApiKey.setId(1L);
		mockApiKey.setUserId(1L);
		mockApiKey.setValue("apikey");
		
		return mockApiKey;
	}
}
