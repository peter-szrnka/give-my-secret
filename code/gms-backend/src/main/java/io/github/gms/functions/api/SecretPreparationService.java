package io.github.gms.functions.api;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.iprestriction.IpRestrictionValidator;
import io.github.gms.functions.secret.ApiKeyRestrictionEntity;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.dto.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.gms.common.types.ErrorCode.GMS_003;
import static io.github.gms.common.types.ErrorCode.GMS_016;
import static io.github.gms.common.types.ErrorCode.GMS_017;
import static io.github.gms.common.types.ErrorCode.GMS_022;
import static io.github.gms.common.types.ErrorCode.GMS_023;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecretPreparationService {
    private final SecretRepository secretRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private final IpRestrictionService ipRestrictionService;
    private final IpRestrictionValidator ipRestrictionValidator;

    public SecretEntity getSecretEntity(GetSecretRequestDto dto) {
        ApiKeyEntity apiKeyEntity = apiKeyRepository.findByValueAndStatus(dto.getApiKey(), EntityStatus.ACTIVE);

        validateApiKey(apiKeyEntity);
        validateUserByApiKey(apiKeyEntity);

        SecretEntity secretEntity = secretRepository.findByUserIdAndSecretIdAndStatus(apiKeyEntity.getUserId(),
                dto.getSecretId(), EntityStatus.ACTIVE).orElseThrow(() -> {
            log.warn("Secret not found"); return new GmsException("Secret is not available!", GMS_022); });

        // Ip Restriction
        IpRestrictionPatterns patterns = ipRestrictionService.checkIpRestrictionsBySecret(secretEntity.getId());
        if (ipRestrictionValidator.isIpAddressBlocked(patterns.getItems())) {
            throw new GmsException("You are not allowed to get this secret from your IP address!", GMS_023);
        }

        // API key restriction
        List<ApiKeyRestrictionEntity> restrictions = apiKeyRestrictionRepository
                .findAllByUserIdAndSecretId(apiKeyEntity.getUserId(), secretEntity.getId());

        if (!restrictions.isEmpty() && restrictions.stream().noneMatch(restriction -> restriction.getApiKeyId().equals(apiKeyEntity.getId()))) {
            log.warn("You are not allowed to use this API key for this secret!");
            throw new GmsException("You are not allowed to use this API key for this secret!", GMS_017);
        }

        return secretEntity;
    }

    private void validateApiKey(ApiKeyEntity apiKeyEntity) {
        if (apiKeyEntity == null) {
            log.warn("API key not found");
            throw new GmsException("Wrong API key!", GMS_016);
        }
    }

    private void validateUserByApiKey(ApiKeyEntity apiKeyEntity) {
        userRepository.findById(apiKeyEntity.getUserId()).ifPresentOrElse(entity -> {
        }, () -> {
            log.warn("User not found");
            throw new GmsException("User not found!", GMS_003);
        });
    }
}