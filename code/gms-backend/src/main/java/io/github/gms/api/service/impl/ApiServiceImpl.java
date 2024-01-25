package io.github.gms.api.service.impl;

import io.github.gms.api.service.ApiService;
import io.github.gms.api.service.KeystoreValidatorService;
import io.github.gms.api.service.SecretPreparationService;
import io.github.gms.common.enums.SecretType;
import io.github.gms.secure.dto.GetSecretRequestDto;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    private final SecretPreparationService secretPreparationService;
    private final KeystoreValidatorService keystoreValidatorService;

    public ApiServiceImpl(
            CryptoService cryptoService,
            SecretPreparationService secretPreparationService,
            KeystoreValidatorService keystoreValidatorService
    ) {
        this.cryptoService = cryptoService;
        this.secretPreparationService = secretPreparationService;
        this.keystoreValidatorService = keystoreValidatorService;
    }

    @Override
    @Cacheable
    public Map<String, String> getSecret(GetSecretRequestDto dto) {
        log.info("Searching for secret={}", dto.getSecretId());

        // Validate API key, user, then get the secret
        SecretEntity secretEntity = secretPreparationService.getSecretEntity(dto);

        // Validate the Keystore
        keystoreValidatorService.validateSecretKeystore(secretEntity);

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