package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.functions.apikey.ApiKeyDto;
import io.github.gms.functions.apikey.ApiKeyListDto;
import io.github.gms.functions.apikey.SaveApiKeyRequestDto;
import io.github.gms.functions.apikey.ApiKeyEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyConverter extends GmsConverter<ApiKeyListDto, ApiKeyEntity> {

	ApiKeyEntity toNewEntity(SaveApiKeyRequestDto dto);

	ApiKeyEntity toEntity(ApiKeyEntity apiKeyEntity, SaveApiKeyRequestDto dto);
	
	ApiKeyDto toDto(ApiKeyEntity entity);
}
