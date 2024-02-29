package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.GmsConverter;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretConverter extends GmsConverter<SecretListDto, SecretEntity> {

	SecretEntity toEntity(SecretEntity secretEntity, SaveSecretRequestDto dto);

	SecretEntity toNewEntity(SaveSecretRequestDto dto);

	SecretDto toDto(SecretEntity entity, List<ApiKeyRestrictionEntity> result);
}
