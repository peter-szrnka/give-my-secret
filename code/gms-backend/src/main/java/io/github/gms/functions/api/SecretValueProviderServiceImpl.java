package io.github.gms.functions.api;

import io.github.gms.common.enums.SecretType;
import io.github.gms.common.service.CryptoService;
import io.github.gms.functions.secret.SecretEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_API_GENERATOR;
import static io.github.gms.common.util.Constants.VALUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CACHE_API, keyGenerator = CACHE_API_GENERATOR)
public class SecretValueProviderServiceImpl implements SecretValueProviderService {

    private final KeystoreValidatorService keystoreValidatorService;
    private final CryptoService cryptoService;

    @Override
    @Cacheable
    public Map<String, String> getSecretValue(SecretEntity secretEntity) {
        log.info("Retrieve secretValue from entity={}", secretEntity.getId());

        // Validate the Keystore
        keystoreValidatorService.validateSecretKeystore(secretEntity);

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
