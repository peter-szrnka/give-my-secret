package io.github.gms.secure.converter.impl;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.converter.ApiKeyConverter;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class ApiKeyConverterImpl implements ApiKeyConverter {

	private final Clock clock;

	public ApiKeyConverterImpl(Clock clock) {
		this.clock = clock;
	}

	@Override
	public ApiKeyEntity toNewEntity(SaveApiKeyRequestDto dto) {
		ApiKeyEntity entity = new ApiKeyEntity();
		entity.setUserId(dto.getUserId());
		entity.setName(dto.getName());
		entity.setValue(dto.getValue());
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setDescription(dto.getDescription());
		entity.setStatus(EntityStatus.ACTIVE);
		return entity;
	}

	@Override
	public ApiKeyEntity toEntity(ApiKeyEntity entity, SaveApiKeyRequestDto dto) {
		entity.setId(dto.getId());
		entity.setUserId(dto.getUserId());
		if (dto.getValue() != null) {
			entity.setValue(dto.getValue());
		}
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		if (dto.getStatus() != null) {
			entity.setStatus(dto.getStatus());
		}
		return entity;
	}

	@Override
	public ApiKeyDto toDto(ApiKeyEntity entity) {
		ApiKeyDto dto = new ApiKeyDto();
		dto.setId(entity.getId());
		dto.setUserId(entity.getUserId());
		dto.setName(entity.getName());
		dto.setDescription(entity.getDescription());
		dto.setStatus(entity.getStatus());
		dto.setCreationDate(entity.getCreationDate());
		dto.setValue(entity.getValue());
		return dto;
	}

	@Override
	public ApiKeyListDto toDtoList(Page<ApiKeyEntity> resultList) {
		return new ApiKeyListDto(resultList.toList().stream().map(this::toDto).collect(Collectors.toList()));
	}
}
