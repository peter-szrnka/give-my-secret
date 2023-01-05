package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link SecretRotationServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationServiceImplTest extends AbstractUnitTest {

	private static final String SECRET_VALUE = "12345678";

	@Mock
	private SecretRepository secretRepository;

	@Mock
	private CryptoService cryptoService;
	
	@InjectMocks
	private SecretRotationServiceImpl service;
	
	@Test
	void shouldNotRotate() {
		// arrange
		SecretEntity mockSecret = new SecretEntity();
		mockSecret.setValue("abcdefgh");
		
		when(cryptoService.decrypt(any(SecretEntity.class))).thenThrow(IllegalArgumentException.class);

		// act
		service.rotateSecret(mockSecret);
		
		// assert
		ArgumentCaptor<SecretEntity> secretEntityCaptor = ArgumentCaptor.forClass(SecretEntity.class);
		
		verify(cryptoService).decrypt(secretEntityCaptor.capture());
		SecretEntity capturedSecret = secretEntityCaptor.getValue();
		assertEquals("abcdefgh", capturedSecret.getValue());
		
		verify(secretRepository).save(secretEntityCaptor.capture());
		capturedSecret = secretEntityCaptor.getValue();
		assertEquals(EntityStatus.DISABLED, capturedSecret.getStatus());
		assertEquals("abcdefgh", capturedSecret.getValue());
		
		verify(clock, never()).instant();
		verify(clock, never()).getZone();
	}
	
	@Test
	void shouldRotate() {
		// arrange
		setupClock();
		SecretEntity mockSecret = TestUtils.createSecretEntity();
		
		when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn(SECRET_VALUE);

		// act
		service.rotateSecret(mockSecret);
		
		// assert
		ArgumentCaptor<SecretEntity> secretEntityCaptor = ArgumentCaptor.forClass(SecretEntity.class);
		
		verify(cryptoService).decrypt(secretEntityCaptor.capture());
		SecretEntity capturedSecret = secretEntityCaptor.getValue();
		assertEquals(SECRET_VALUE, capturedSecret.getValue());
		
		verify(cryptoService).encrypt(secretEntityCaptor.capture());
		capturedSecret = secretEntityCaptor.getValue();
		assertEquals(SECRET_VALUE, capturedSecret.getValue());
		
		verify(secretRepository).save(secretEntityCaptor.capture());
		capturedSecret = secretEntityCaptor.getValue();
		assertEquals(EntityStatus.ACTIVE, capturedSecret.getStatus());
		assertEquals(SECRET_VALUE, capturedSecret.getValue());
	}
	
	@Test
	void shouldNotRotateById() {
		// arrange
		SecretEntity mockSecret = new SecretEntity();
		mockSecret.setValue("abcdefgh");

		when(secretRepository.findById(anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.rotateSecretById(1L));

		// assert
		assertEquals("Secret not found!", exception.getMessage());
		verify(secretRepository).findById(anyLong());
		verify(clock, never()).instant();
		verify(clock, never()).getZone();
	}
	
	@Test
	void shouldRotateById() {
		// arrange
		setupClock();
		SecretEntity mockSecret = new SecretEntity();
		mockSecret.setValue("abcdefgh");
		
		when(cryptoService.decrypt(any(SecretEntity.class))).thenReturn(SECRET_VALUE);
		when(secretRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createSecretEntity()));

		// act
		service.rotateSecretById(1L);

		// assert
		verify(secretRepository).findById(anyLong());
		
		ArgumentCaptor<SecretEntity> secretEntityCaptor = ArgumentCaptor.forClass(SecretEntity.class);
		
		verify(cryptoService).decrypt(secretEntityCaptor.capture());
		SecretEntity capturedSecret = secretEntityCaptor.getValue();
		assertEquals(SECRET_VALUE, capturedSecret.getValue());
		
		verify(secretRepository).save(secretEntityCaptor.capture());
		capturedSecret = secretEntityCaptor.getValue();
		assertEquals(EntityStatus.ACTIVE, capturedSecret.getStatus());
		assertEquals(SECRET_VALUE, capturedSecret.getValue());
	}
}
