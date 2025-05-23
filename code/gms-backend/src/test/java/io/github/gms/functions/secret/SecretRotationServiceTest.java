package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.types.GmsException;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretRotationServiceTest extends AbstractUnitTest {

	private static final String SECRET_VALUE = "12345678";

	private Clock clock;
	private SecretRepository secretRepository;
	private CryptoService cryptoService;
	private SecretRotationService service;

	@BeforeEach
	void setup() {
		clock = mock(Clock.class);
		secretRepository = mock(SecretRepository.class);
		cryptoService = mock(CryptoService.class);
		service = new SecretRotationService(clock, secretRepository, cryptoService);
	}
	
	@Test
	void rotateSecret_whenDecryptThrowsException_thenRotateSkipped() {
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
	void rotateSecret_whenInputProvided_thenRotateSecret() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
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
		assertEquals("2023-06-29T00:00Z", capturedSecret.getLastRotated().toString());
	}
	
	@Test
	void rotateSecretById_whenSecretNotFound_thenThrowException() {
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
	void rotateSecretById_whenInputProvided_thenRotateSecret() {
		// arrange
		setupClock(clock);
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
