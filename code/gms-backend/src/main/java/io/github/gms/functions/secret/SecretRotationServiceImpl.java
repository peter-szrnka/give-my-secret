package io.github.gms.functions.secret;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class SecretRotationServiceImpl implements SecretRotationService {

	private final Clock clock;
	private final SecretRepository secretRepository;
	private final CryptoService cryptoService;

	public SecretRotationServiceImpl(Clock clock, SecretRepository secretRepository, CryptoService cryptoService) {
		this.clock = clock;
		this.secretRepository = secretRepository;
		this.cryptoService = cryptoService;
	}

	@Override
	@Async("secretRotationExecutor")
	public void rotateSecret(SecretEntity secretEntity) {
		rotateSecretEntity(secretEntity);
	}

	@Override
	public void rotateSecretById(Long id) {
		log.info("Rotate secret={}", id);
		SecretEntity entity = secretRepository.findById(id).orElseThrow(() -> new GmsException("Secret not found!"));
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
