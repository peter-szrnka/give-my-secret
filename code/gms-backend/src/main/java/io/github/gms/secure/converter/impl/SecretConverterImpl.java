package io.github.gms.secure.converter.impl;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.converter.SecretConverter;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.SecretEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class SecretConverterImpl implements SecretConverter {

	private final Clock clock;

	public SecretConverterImpl(Clock clock) {
		this.clock = clock;
	}

	@Override
	public SecretEntity toEntity(SecretEntity entity, SaveSecretRequestDto dto) {
		entity.setKeystoreAliasId(dto.getKeystoreAliasId());
		entity.setSecretId(dto.getSecretId());
		entity.setUserId(dto.getUserId());
		entity.setReturnDecrypted(dto.isReturnDecrypted());
		entity.setRotationEnabled(dto.isRotationEnabled());
		entity.setLastUpdated(ZonedDateTime.now(clock));
		entity.setType(dto.getType());
		
		if (dto.getValue() != null) {
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
		entity.setId(dto.getId());
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
		return new SecretListDto(resultList.toList().stream().map(result -> toDto(result, null)).collect(Collectors.toList()));
	}
}
