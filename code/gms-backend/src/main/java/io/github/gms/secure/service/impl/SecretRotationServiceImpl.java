package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.SecretRotationService;
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
