package io.github.gms.secure.converter;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.secure.dto.KeystoreAliasDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import io.github.gms.secure.entity.KeystoreEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreConverter extends GmsConverter<KeystoreListDto, KeystoreEntity> {

	KeystoreEntity toNewEntity(SaveKeystoreRequestDto dto, MultipartFile file);

	KeystoreEntity toEntity(KeystoreEntity entity, SaveKeystoreRequestDto dto);
	
	KeystoreDto toDto(KeystoreEntity entity, List<KeystoreAliasEntity> aliases);

	KeystoreAliasEntity toAliasEntity(Long keystoreId, KeystoreAliasDto alias);
}
