package io.github.gms.functions.keystore;

import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.functions.keystore.KeystoreAliasDto;
import io.github.gms.functions.keystore.KeystoreDto;
import io.github.gms.functions.keystore.KeystoreListDto;
import io.github.gms.functions.keystore.SaveKeystoreRequestDto;
import io.github.gms.functions.keystore.KeystoreAliasEntity;
import io.github.gms.functions.keystore.KeystoreEntity;
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
