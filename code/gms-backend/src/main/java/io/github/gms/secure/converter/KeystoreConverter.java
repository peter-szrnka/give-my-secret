package io.github.gms.secure.converter;

import org.springframework.web.multipart.MultipartFile;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.common.entity.KeystoreEntity;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreConverter extends GmsConverter<KeystoreListDto, KeystoreEntity> {

	KeystoreEntity toNewEntity(SaveKeystoreRequestDto dto, MultipartFile file);

	KeystoreEntity toEntity(KeystoreEntity entity, SaveKeystoreRequestDto dto, MultipartFile file);
	
	KeystoreDto toDto(KeystoreEntity entity);
}
