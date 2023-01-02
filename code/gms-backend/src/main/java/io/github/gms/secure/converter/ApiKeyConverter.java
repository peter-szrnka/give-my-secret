package io.github.gms.secure.converter;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.common.entity.ApiKeyEntity;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyConverter extends GmsConverter<ApiKeyListDto, ApiKeyEntity> {

	ApiKeyEntity toNewEntity(SaveApiKeyRequestDto dto);

	ApiKeyEntity toEntity(ApiKeyEntity apiKeyEntity, SaveApiKeyRequestDto dto);
	
	ApiKeyDto toDto(ApiKeyEntity entity);
}
