package io.github.gms.functions.keystore;

import io.github.gms.common.abstraction.GmsConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class KeystoreConverter implements GmsConverter<KeystoreListDto, KeystoreEntity> {

	private final Clock clock;

	public KeystoreEntity toNewEntity(SaveKeystoreRequestDto dto, MultipartFile file) {
		KeystoreEntity entity = new KeystoreEntity();

		entity.setName(dto.getName());
		entity.setUserId(dto.getUserId());
		entity.setDescription(dto.getDescription());
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setCredential(dto.getCredential());
		if (file != null) {
			entity.setFileName(file.getOriginalFilename());
		}
		entity.setStatus(dto.getStatus());
		entity.setType(dto.getType());

		return entity;
	}

	public KeystoreEntity toEntity(KeystoreEntity entity, SaveKeystoreRequestDto dto) {
		entity.setId(dto.getId());

		entity.setName(dto.getName());
		entity.setUserId(dto.getUserId());
		entity.setDescription(dto.getDescription());
		entity.setCredential(dto.getCredential());
		entity.setStatus(dto.getStatus());
		entity.setType(dto.getType());

		return entity;
	}

	public KeystoreDto toDto(KeystoreEntity entity, List<KeystoreAliasEntity> aliasList) {
		KeystoreDto dto = new KeystoreDto();
		dto.setId(entity.getId());

		dto.setUserId(entity.getUserId());
		dto.setDescription(entity.getDescription());
		dto.setFileName(entity.getFileName());
		dto.setStatus(entity.getStatus());
		dto.setName(entity.getName());
		dto.setType(entity.getType());
		dto.setCreationDate(entity.getCreationDate());
		dto.setCredential(entity.getCredential());

		if (!CollectionUtils.isEmpty(aliasList)) {
			dto.setAliases(aliasList.stream().map(this::convertToAliasDto).toList());
		}

		return dto;
	}

	@Override
	public KeystoreListDto toDtoList(Page<KeystoreEntity> resultList) {
		List<KeystoreDto> results = resultList.toList().stream().map(entity -> toDto(entity, null)).toList();
		return KeystoreListDto.builder().resultList(results).totalElements(resultList.getTotalElements()).build();
	}

	public KeystoreAliasEntity toAliasEntity(Long keystoreId, KeystoreAliasDto dto) {
		KeystoreAliasEntity entity = new KeystoreAliasEntity();

		entity.setId(dto.getId());
		entity.setKeystoreId(keystoreId);
		entity.setAlias(dto.getAlias());
		entity.setAliasCredential(dto.getAliasCredential());
		entity.setAlgorithm(dto.getAlgorithm());

		return entity;
	}

	private KeystoreAliasDto convertToAliasDto(KeystoreAliasEntity entity) {
		KeystoreAliasDto dto = new KeystoreAliasDto();

		dto.setId(entity.getId());
		dto.setAlias(entity.getAlias());
		dto.setAliasCredential(entity.getAliasCredential());
		dto.setAlgorithm(entity.getAlgorithm());

		return dto;
	}
}
