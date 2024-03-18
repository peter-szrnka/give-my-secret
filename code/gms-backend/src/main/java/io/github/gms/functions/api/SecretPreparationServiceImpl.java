package io.github.gms.functions.api;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.iprestriction.IpRestrictionValidator;
import io.github.gms.functions.secret.ApiKeyRestrictionEntity;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecretPreparationServiceImpl implements SecretPreparationService {
    private final SecretRepository secretRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private final IpRestrictionService ipRestrictionService;
    private final IpRestrictionValidator ipRestrictionValidator;

    @Override
    public SecretEntity getSecretEntity(GetSecretRequestDto dto) {
        ApiKeyEntity apiKeyEntity = apiKeyRepository.findByValueAndStatus(dto.getApiKey(), EntityStatus.ACTIVE);

        validateApiKey(apiKeyEntity);
        validateUserByApiKey(apiKeyEntity);

        SecretEntity secretEntity = secretRepository.findByUserIdAndSecretIdAndStatus(apiKeyEntity.getUserId(),
                dto.getSecretId(), EntityStatus.ACTIVE).orElseThrow(() -> {
            log.warn("Secret not found"); return new GmsException("Secret is not available!"); });

        // Ip Restriction
        List<IpRestrictionPattern> patterns = ipRestrictionService.checkIpRestrictionsBySecret(secretEntity.getId());
        if (ipRestrictionValidator.isIpAddressBlocked(patterns)) {
            throw new GmsException("You are not allowed to get this secret from your IP address!");
        }

        // API key restriction
        List<ApiKeyRestrictionEntity> restrictions = apiKeyRestrictionRepository
                .findAllByUserIdAndSecretId(apiKeyEntity.getUserId(), secretEntity.getId());

        if (!restrictions.isEmpty() && restrictions.stream().noneMatch(restriction -> restriction.getApiKeyId().equals(apiKeyEntity.getId()))) {
            log.warn("You are not allowed to use this API key for this secret!");
            throw new GmsException("You are not allowed to use this API key for this secret!");
        }

        return secretEntity;
    }

    private void validateApiKey(ApiKeyEntity apiKeyEntity) {
        if (apiKeyEntity == null) {
            log.warn("API key not found");
            throw new GmsException("Wrong API key!");
        }
    }

    private void validateUserByApiKey(ApiKeyEntity apiKeyEntity) {
        userRepository.findById(apiKeyEntity.getUserId()).ifPresentOrElse(entity -> {
        }, () -> {
            log.warn("User not found");
            throw new GmsException("User not found!");
        });
    }
}