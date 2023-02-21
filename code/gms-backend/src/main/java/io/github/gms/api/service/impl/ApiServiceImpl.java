package io.github.gms.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.gms.api.service.ApiService;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.dto.CredentialPairApiResponseDto;
import io.github.gms.secure.dto.GetSecretRequestDto;
import io.github.gms.secure.dto.SimpleApiResponseDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.CryptoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class ApiServiceImpl implements ApiService {
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private SecretRepository secretRepository;

	@Autowired
	private ApiKeyRepository apiKeyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private KeystoreRepository keystoreRepository;
	
	@Autowired
	private KeystoreAliasRepository keystoreAliasRepository;

	@Autowired
	private ApiKeyRestrictionRepository apiKeyRestrictionRepository;

	@Override
	public ApiResponseDto getSecret(GetSecretRequestDto dto) {
		log.info("Searching for secret={}", dto.getSecretId());
		ApiKeyEntity apiKeyEntity = apiKeyRepository.findByValueAndStatus(dto.getApiKey(), EntityStatus.ACTIVE);

		if (apiKeyEntity == null) {
			log.warn("API key not found");
			throw new GmsException("Wrong API key!");
		}

		userRepository.findById(apiKeyEntity.getUserId()).ifPresentOrElse(entity -> {}, () -> {
			log.warn("User not found");
			throw new GmsException("User not found!");
		});

		SecretEntity secretEntity = secretRepository.findByUserIdAndSecretIdAndStatus(apiKeyEntity.getUserId(),
				dto.getSecretId(), EntityStatus.ACTIVE);

		if (secretEntity == null) {
			log.warn("Secret not found");
			throw new GmsException("Secret is not available!");
		}

		List<ApiKeyRestrictionEntity> restrictions = apiKeyRestrictionRepository
				.findAllByUserIdAndSecretId(apiKeyEntity.getUserId(), secretEntity.getId());
		
		if (!restrictions.isEmpty() && restrictions.stream().noneMatch(restriction -> restriction.getApiKeyId().equals(apiKeyEntity.getId()))) {
			log.warn("You are not allowed to use this API key for this secret!");
			throw new GmsException("You are not allowed to use this API key for this secret!");
		}
		
		KeystoreAliasEntity aliasEntity = keystoreAliasRepository.findById(secretEntity.getKeystoreAliasId()).orElseThrow(() -> {
			log.warn("Keystore alias not found");
			throw new GmsException("Keystore alias is not available!");
		});

		if (keystoreRepository
				.findByIdAndUserIdAndStatus(aliasEntity.getKeystoreId(), secretEntity.getUserId(), EntityStatus.ACTIVE)
				.isEmpty()) {
			log.warn("Keystore is not active");
			throw new GmsException("Invalid keystore!");
		}
		
		return getSecretValue(secretEntity);
	}

	private ApiResponseDto getSecretValue(SecretEntity entity) {
		String value = (entity.isReturnDecrypted()) ? cryptoService.decrypt(entity) : entity.getValue();
		
		if (SecretType.SIMPLE_CREDENTIAL == entity.getType()) {
			return new SimpleApiResponseDto(value);
		}

		try {
			return entity.isReturnDecrypted() ? objectMapper.readValue(value, CredentialPairApiResponseDto.class) : new SimpleApiResponseDto(value);
		} catch (JsonProcessingException e) {
			throw new GmsException(e);
		}
	}
}
