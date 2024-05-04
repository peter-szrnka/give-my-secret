package io.github.gms.functions.secret;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.types.GmsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;

import static io.github.gms.common.types.ErrorCode.GMS_002;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecretRotationService {

	private final Clock clock;
	private final SecretRepository secretRepository;
	private final CryptoService cryptoService;

	@Async("secretRotationExecutor")
	public void rotateSecret(SecretEntity secretEntity) {
		rotateSecretEntity(secretEntity);
	}

	public void rotateSecretById(Long id) {
		log.info("Rotate secret={}", id);
		SecretEntity entity = secretRepository.findById(id).orElseThrow(() -> new GmsException("Secret not found!", GMS_002));
		rotateSecretEntity(entity);
	}

	private void rotateSecretEntity(SecretEntity entity) {
		try {
			String decrypted = cryptoService.decrypt(entity);
			entity.setValue(decrypted);
			cryptoService.encrypt(entity);
			entity.setLastRotated(ZonedDateTime.now(clock));
		} catch (Exception e) {
			entity.setStatus(EntityStatus.DISABLED);
		}

		secretRepository.save(entity);
	}
}
