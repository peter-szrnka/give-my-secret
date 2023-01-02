package io.github.gms.secure.converter.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.github.gms.common.entity.KeystoreEntity;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class KeystoreConverterImpl implements KeystoreConverter {
	
	@Autowired
	private Clock clock;

	@Override
	public KeystoreEntity toNewEntity(SaveKeystoreRequestDto dto, MultipartFile file) {
		KeystoreEntity entity = new KeystoreEntity();

		entity.setName(dto.getName());
		entity.setUserId(dto.getUserId());
		entity.setAlias(dto.getAlias());
		entity.setDescription(dto.getDescription());
		entity.setCreationDate(LocalDateTime.now(clock));
		entity.setCredential(dto.getCredential());
		entity.setName(dto.getName());
		if (file != null) {
			entity.setFileName(file.getOriginalFilename());
		}
		entity.setStatus(dto.getStatus());
		entity.setAliasCredential(dto.getAliasCredential());
		entity.setType(dto.getType());

		return entity;
	}

	@Override
	public KeystoreEntity toEntity(KeystoreEntity entity, SaveKeystoreRequestDto dto, MultipartFile file) {
		entity.setId(dto.getId());
		
		entity.setName(dto.getName());
		entity.setUserId(dto.getUserId());
		entity.setAlias(dto.getAlias());
		entity.setDescription(dto.getDescription());
		entity.setCredential(dto.getCredential());
		entity.setName(dto.getName());
		if (file != null) {
			entity.setFileName(file.getOriginalFilename());
		}
		entity.setStatus(dto.getStatus());
		entity.setAliasCredential(dto.getAliasCredential());
		entity.setType(dto.getType());

		return entity;
	}

	@Override
	public KeystoreDto toDto(KeystoreEntity entity) {
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
		dto.setAlias(entity.getAlias());
		dto.setAliasCredential(entity.getAliasCredential());

		return dto;
	}

	@Override
	public KeystoreListDto toDtoList(Page<KeystoreEntity> resultList) {
		return new KeystoreListDto(resultList.toList().stream().map(this::toDto).collect(Collectors.toList()));
	}
}
