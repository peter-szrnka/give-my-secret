package io.github.gms.secure.converter.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.github.gms.common.entity.ApiKeyRestrictionEntity;
import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.converter.SecretConverter;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class SecretConverterImpl implements SecretConverter {
	
	@Autowired
	private Clock clock;

	@Override
	public SecretEntity toEntity(SecretEntity entity, SaveSecretRequestDto dto) {
		entity.setKeystoreId(dto.getKeystoreId());
		entity.setSecretId(dto.getSecretId());
		entity.setUserId(dto.getUserId());
		entity.setReturnDecrypted(dto.isReturnDecrypted());
		entity.setRotationEnabled(dto.isRotationEnabled());
		entity.setLastUpdated(LocalDateTime.now(clock));
		
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
		entity.setKeystoreId(dto.getKeystoreId());
		entity.setUserId(dto.getUserId());
		entity.setValue(dto.getValue());
		entity.setCreationDate(LocalDateTime.now(clock));
		entity.setLastUpdated(LocalDateTime.now(clock));
		entity.setLastRotated(LocalDateTime.now(clock));
		entity.setRotationPeriod(dto.getRotationPeriod());
		entity.setReturnDecrypted(dto.isReturnDecrypted());
		entity.setRotationEnabled(dto.isRotationEnabled());
		entity.setStatus(EntityStatus.ACTIVE);
		return entity;
	}

	@Override
	public SecretDto toDto(SecretEntity entity, List<ApiKeyRestrictionEntity> apiKeyRestrictions) {
		SecretDto dto = new SecretDto();
		dto.setId(entity.getId());
		dto.setSecretId(entity.getSecretId());
		dto.setKeystoreId(entity.getKeystoreId());
		dto.setUserId(entity.getUserId());
		dto.setCreationDate(entity.getCreationDate());
		dto.setLastUpdated(entity.getLastUpdated());
		dto.setLastRotated(entity.getLastRotated());
		dto.setRotationPeriod(entity.getRotationPeriod());
		dto.setReturnDecrypted(entity.isReturnDecrypted());
		dto.setRotationEnabled(entity.isRotationEnabled());
		dto.setStatus(entity.getStatus());
		
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
