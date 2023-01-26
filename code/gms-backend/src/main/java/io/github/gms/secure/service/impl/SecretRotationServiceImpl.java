package io.github.gms.secure.service.impl;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.SecretRotationService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class SecretRotationServiceImpl implements SecretRotationService {
	
	@Autowired
	private Clock clock;

	@Autowired
	private SecretRepository secretRepository;

	@Autowired
	private CryptoService cryptoService;

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
		String originalValue = entity.getValue();

		try {
			String decrypted = cryptoService.decrypt(entity);
			entity.setValue(decrypted);
			cryptoService.encrypt(entity);
			entity.setLastRotated(ZonedDateTime.now(clock));
		} catch (Exception e) {
			entity.setStatus(EntityStatus.DISABLED);
			entity.setValue(originalValue);
		}

		secretRepository.save(entity);
	}
}
