package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.GmsConverter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyConverter extends GmsConverter<ApiKeyListDto, ApiKeyEntity> {

	ApiKeyEntity toNewEntity(SaveApiKeyRequestDto dto);

	ApiKeyEntity toEntity(ApiKeyEntity apiKeyEntity, SaveApiKeyRequestDto dto);
	
	ApiKeyDto toDto(ApiKeyEntity entity);
}
