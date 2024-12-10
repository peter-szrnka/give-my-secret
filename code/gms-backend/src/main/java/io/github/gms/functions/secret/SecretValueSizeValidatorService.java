package io.github.gms.functions.secret;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gms.common.dto.BooleanValueDto;
import io.github.gms.common.model.GetKeystore;
import io.github.gms.common.model.KeystorePair;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.keystore.*;
import io.github.gms.functions.secret.dto.SecretValueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyStore;

import static io.github.gms.common.types.ErrorCode.GMS_002;
import static io.github.gms.common.util.Constants.SLASH;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecretValueSizeValidatorService {

    private final KeystoreRepository keystoreRepository;
    private final KeystoreAliasRepository keystoreAliasRepository;
    private final KeystoreDataService keystoreDataService;
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;

    @Value("${config.location.keystore.path}")
    private String keystorePath;

    public BooleanValueDto validateValueLength(SecretValueDto dto) {
        try {
            KeystoreEntity keystoreEntity = keystoreRepository.findById(dto.getKeystoreId())
                    .orElseThrow(() -> new GmsException("KeystoreEntity not found!", GMS_002));

            KeystoreAliasEntity keystoreAliasEntity = keystoreAliasRepository.findById(dto.getKeystoreAliasId())
                    .orElseThrow(() -> new GmsException("KeystoreAliasEntity not found!", GMS_002));

            KeyStore keystore = keystoreDataService.getKeyStore(GetKeystore.builder()
                    .keystoreEntity(keystoreEntity)
                    .keystorePath(keystorePath + keystoreEntity.getUserId() + SLASH + keystoreEntity.getFileName())
                    .build());

            String stringValue = objectMapper.writeValueAsString(dto.getSecretValues());
            String encryptedValue = cryptoService.encrypt(stringValue, new KeystorePair(keystoreAliasEntity, keystore));

            log.info("Encrypted secret value size: {}", encryptedValue.length());
            return new BooleanValueDto(true);
        } catch (Exception e) {
            return new BooleanValueDto(false);
        }
    }
}