package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.util.HttpUtils;
import io.github.gms.common.util.MdcUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.gms.common.util.HttpUtils.getClientIpAddress;
import static java.util.stream.Collectors.toSet;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "ipRestrictionCache")
public class IpRestrictionServiceImpl implements IpRestrictionService {

    private final IpRestrictionRepository repository;
    private final IpRestrictionConverter converter;
    private final HttpServletRequest httpServletRequest;

    @Override
    public SaveEntityResponseDto save(IpRestrictionDto dto) {
        // TODO Create another DTO
        if (dto.getId() != null && !dto.isGlobal()) {
            throw new GmsException("Only global IP restrictions allowed to save with this service!");
        }

        IpRestrictionEntity entity = converter.toEntity(dto);
        entity.setUserId(MdcUtils.getUserId());
        entity.setGlobal(true);
        entity = repository.save(entity);
        return new SaveEntityResponseDto(entity.getId());
    }

    @Override
    public IpRestrictionListDto list(PagingDto dto) {
        Page<IpRestrictionEntity> results = repository.findAllGlobal(ConverterUtils.createPageable(dto));
        return IpRestrictionListDto.builder()
                .resultList(results.toList().stream()
                        .map(converter::toDto)
                        .toList())
                .totalElements(results.getTotalElements())
                .build();
    }

    @Override
    public IpRestrictionDto getById(Long id) {
        // TODO Check it is really a global IP restriction
        return converter.toDto(repository.findGlobalById(id));
    }

    @Override
    public void delete(Long id) {
        // TODO Check it is really a global IP restriction
        repository.deleteById(id);
    }

    @Override
    public void updateIpRestrictionsForSecret(Long secretId, List<IpRestrictionDto> ipRestrictions) {
        ipRestrictions.forEach(ipRestriction -> ipRestriction.setSecretId(secretId));

        Set<Long> existingEntityIds = findAll(secretId)
                .stream()
                .map(IpRestrictionEntity::getId)
                .collect(toSet());
        Set<Long> newIds = ipRestrictions.stream()
                .map(IpRestrictionDto::getId)
                .filter(Objects::nonNull)
                .collect(toSet());

        // Save each entity
        ipRestrictions.forEach(dto -> repository.save(converter.toEntity(dto)));

        // Remove old entities
        repository.deleteAllById(existingEntityIds.stream().filter(id -> !newIds.contains(id)).collect(toSet()));
    }

    @Override
    public List<IpRestrictionDto> getAllBySecretId(Long secretId) {
        return converter.toDtoList(findAll(secretId));
    }

    @Override
    @Cacheable
    public void checkIpRestrictionsBySecret(Long secretId) {
        List<IpRestrictionPattern> patterns = converter.toModelList(findAll(secretId));
        validateByPatterns(patterns);
    }

    @Override
    public void checkGlobalIpRestrictions() {
        List<IpRestrictionPattern> patterns = converter.toModelList(repository.findAllGlobal());
        validateByPatterns(patterns);
    }

    private List<IpRestrictionEntity> findAll(Long secretId) {
        return repository.findAllBySecretId(secretId);
    }

    private void validateByPatterns(List<IpRestrictionPattern> patterns) {
        String ipAddress = getClientIpAddress(httpServletRequest);
        log.info("Client IP address: {}", ipAddress);

        if (patterns.isEmpty()) {
            return;
        }

        boolean ipIsNotAllowed = patterns.stream().anyMatch(p -> p.isAllow() && !ipAddressMatches(p, ipAddress));
        boolean ipIsBlocked = patterns.stream().anyMatch(p -> !p.isAllow() && ipAddressMatches(p, ipAddress));

        if (!HttpUtils.WHITELISTED_ADDRESSES.contains(ipAddress) && (ipIsNotAllowed || ipIsBlocked)) {
            throw new GmsException("You are not allowed to get this secret from your IP address!");
        }
    }

    private static boolean ipAddressMatches(IpRestrictionPattern pattern, String ipAddress) {
        Pattern p = Pattern.compile(pattern.getIpPattern());
        Matcher matcher = p.matcher(ipAddress);
        return matcher.matches();
    }
}