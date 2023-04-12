package io.github.gms.api.service.impl;

import io.github.gms.api.service.ApiService;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.GetSecretRequestDto;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.VALUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "apiCache", keyGenerator = "apiCacheKeyGenerator")
public class ApiServiceImpl implements ApiService {

    private final CryptoService cryptoService;
    private final SecretRepository secretRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final KeystoreRepository keystoreRepository;
    private final KeystoreAliasRepository keystoreAliasRepository;
    private final ApiKeyRestrictionRepository apiKeyRestrictionRepository;

    public ApiServiceImpl(
            CryptoService cryptoService,
            SecretRepository secretRepository,
            ApiKeyRepository apiKeyRepository,
            UserRepository userRepository,
            KeystoreRepository keystoreRepository,
            KeystoreAliasRepository keystoreAliasRepository,
            ApiKeyRestrictionRepository apiKeyRestrictionRepository
    ) {
        this.cryptoService = cryptoService;
        this.secretRepository = secretRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
        this.keystoreRepository = keystoreRepository;
        this.keystoreAliasRepository = keystoreAliasRepository;
        this.apiKeyRestrictionRepository = apiKeyRestrictionRepository;
    }

    @Override
    @Cacheable
    public Map<String, String> getSecret(GetSecretRequestDto dto) {
        log.info("Searching for secret={}", dto.getSecretId());
        ApiKeyEntity apiKeyEntity = apiKeyRepository.findByValueAndStatus(dto.getApiKey(), EntityStatus.ACTIVE);

        if (apiKeyEntity == null) {
            log.warn("API key not found");
            throw new GmsException("Wrong API key!");
        }

        userRepository.findById(apiKeyEntity.getUserId()).ifPresentOrElse(entity -> {
        }, () -> {
            log.warn("User not found");
            throw new GmsException("User not found!");
        });

        SecretEntity secretEntity = secretRepository.findByUserIdAndSecretIdAndStatus(apiKeyEntity.getUserId(),
                dto.getSecretId(), EntityStatus.ACTIVE).orElseThrow(() -> {
                        	log.warn("Secret not found"); return new GmsException("Secret is not available!"); });

        List<ApiKeyRestrictionEntity> restrictions = apiKeyRestrictionRepository
                .findAllByUserIdAndSecretId(apiKeyEntity.getUserId(), secretEntity.getId());

        if (!restrictions.isEmpty() && restrictions.stream().noneMatch(restriction -> restriction.getApiKeyId().equals(apiKeyEntity.getId()))) {
            log.warn("You are not allowed to use this API key for this secret!");
            throw new GmsException("You are not allowed to use this API key for this secret!");
        }

        KeystoreAliasEntity aliasEntity = keystoreAliasRepository.findById(secretEntity.getKeystoreAliasId()).orElseThrow(() -> {
            log.warn("Keystore alias not found");
            return new GmsException("Keystore alias is not available!");
        });

        if (keystoreRepository
                .findByIdAndUserIdAndStatus(aliasEntity.getKeystoreId(), secretEntity.getUserId(), EntityStatus.ACTIVE)
                .isEmpty()) {
            log.warn("Keystore is not active");
            throw new GmsException("Invalid keystore!");
        }

        return getSecretValue(secretEntity);
    }

    private Map<String, String> getSecretValue(SecretEntity secretEntity) {
        if (!secretEntity.isReturnDecrypted()) {
            // Type field will be added only if it's encrypted
            return Map.of(
                    VALUE, secretEntity.getValue(),
                    "type", secretEntity.getType().name()
            );
        }

        String decryptedValue = cryptoService.decrypt(secretEntity);
        return processDecryptedValue(decryptedValue, secretEntity.getType());
    }

    private static Map<String, String> processDecryptedValue(String decryptedValue, SecretType type) {
        if (SecretType.SIMPLE_CREDENTIAL == type) {
            return Map.of(VALUE, decryptedValue);
        }

        Map<String, String> responseMap = new HashMap<>();
        Stream.of(decryptedValue.split(";")).forEach(item -> {
            String[] elements = item.split(":");
            responseMap.put(elements[0], elements[1]);
        });

        return responseMap;
    }
}