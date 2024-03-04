package io.github.gms.functions.secret;

import io.github.gms.common.enums.EntityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class SecretConverterImpl implements SecretConverter {

	private final Clock clock;

	@Override
	public SecretEntity toEntity(SecretEntity entity, SaveSecretRequestDto dto) {
		entity.setKeystoreAliasId(dto.getKeystoreAliasId());
		entity.setSecretId(dto.getSecretId());
		entity.setUserId(dto.getUserId());
		entity.setReturnDecrypted(dto.isReturnDecrypted());
		entity.setRotationEnabled(dto.isRotationEnabled());
		entity.setLastUpdated(ZonedDateTime.now(clock));
		entity.setType(dto.getType());
		
		if (StringUtils.hasText(dto.getValue())) {
			entity.setValue(dto.getValue());
		}
		
		if (dto.getRotationPeriod() != null) {
			entity.setRotationPeriod(dto.getRotationPeriod());
		}
		
		if (dto.getStatus() != null) {
			entity.setStatus(dto.getStatus());
		}

		return entity;
	}

	@Override
	public SecretEntity toNewEntity(SaveSecretRequestDto dto) {
		SecretEntity entity = new SecretEntity();
		entity.setSecretId(dto.getSecretId());
		entity.setKeystoreAliasId(dto.getKeystoreAliasId());
		entity.setUserId(dto.getUserId());
		entity.setValue(dto.getValue());
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setLastUpdated(ZonedDateTime.now(clock));
		entity.setLastRotated(ZonedDateTime.now(clock));
		entity.setRotationPeriod(dto.getRotationPeriod());
		entity.setReturnDecrypted(dto.isReturnDecrypted());
		entity.setRotationEnabled(dto.isRotationEnabled());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setType(dto.getType());
		return entity;
	}

	@Override
	public SecretDto toDto(SecretEntity entity, List<ApiKeyRestrictionEntity> apiKeyRestrictions) {
		SecretDto dto = new SecretDto();
		dto.setId(entity.getId());
		dto.setSecretId(entity.getSecretId());
		dto.setKeystoreAliasId(entity.getKeystoreAliasId());
		dto.setUserId(entity.getUserId());
		dto.setCreationDate(entity.getCreationDate());
		dto.setLastUpdated(entity.getLastUpdated());
		dto.setLastRotated(entity.getLastRotated());
		dto.setRotationPeriod(entity.getRotationPeriod());
		dto.setReturnDecrypted(entity.isReturnDecrypted());
		dto.setRotationEnabled(entity.isRotationEnabled());
		dto.setStatus(entity.getStatus());
		dto.setType(entity.getType());
		
		if (apiKeyRestrictions != null) {
			dto.setApiKeyRestrictions(apiKeyRestrictions.stream().map(ApiKeyRestrictionEntity::getApiKeyId).collect(Collectors.toSet()));
		}

		return dto;
	}

	@Override
	public SecretListDto toDtoList(Page<SecretEntity> resultList) {
		List<SecretDto> results = resultList.toList().stream().map(result -> toDto(result, null)).toList();
		return SecretListDto.builder().resultList(results).totalElements(resultList.getTotalElements()).build();
	}
}
