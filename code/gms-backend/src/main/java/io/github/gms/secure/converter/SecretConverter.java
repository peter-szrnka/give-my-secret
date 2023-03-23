package io.github.gms.secure.converter;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.SecretEntity;

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
