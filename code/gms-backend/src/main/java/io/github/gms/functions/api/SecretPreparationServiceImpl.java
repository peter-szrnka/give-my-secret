package io.github.gms.functions.api;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.HttpUtils;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionPattern;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.secret.ApiKeyRestrictionEntity;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.gms.common.util.HttpUtils.getClientIpAddress;

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
    private final HttpServletRequest httpServletRequest;

    @Override
    public SecretEntity getSecretEntity(GetSecretRequestDto dto) {
        ApiKeyEntity apiKeyEntity = apiKeyRepository.findByValueAndStatus(dto.getApiKey(), EntityStatus.ACTIVE);

        validateApiKey(apiKeyEntity);
        validateUserByApiKey(apiKeyEntity);

        SecretEntity secretEntity = secretRepository.findByUserIdAndSecretIdAndStatus(apiKeyEntity.getUserId(),
                dto.getSecretId(), EntityStatus.ACTIVE).orElseThrow(() -> {
            log.warn("Secret not found"); return new GmsException("Secret is not available!"); });

        // Ip Restriction
        List<IpRestrictionPattern> patterns = ipRestrictionService.getIpRestrictionsBySecret(secretEntity.getId());

        String ipAddress = getClientIpAddress(httpServletRequest);
        log.info("Client IP address: {}", ipAddress);

        boolean ipIsNotAllowed = patterns.stream().filter(IpRestrictionPattern::isAllow).noneMatch(pattern -> ipAddressMatches(pattern, ipAddress));
        boolean ipIsBlocked = patterns.stream().filter(p -> !p.isAllow()).anyMatch(pattern -> ipAddressMatches(pattern, ipAddress));

        if (!HttpUtils.WHITELISTED_ADDRESSES.contains(ipAddress) && (ipIsNotAllowed || ipIsBlocked)) {
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

    private static boolean ipAddressMatches(IpRestrictionPattern pattern, String ipAddress) {
        Pattern p = Pattern.compile(pattern.getIpPattern());
        Matcher matcher = p.matcher(ipAddress);
        return matcher.matches();
    }
}