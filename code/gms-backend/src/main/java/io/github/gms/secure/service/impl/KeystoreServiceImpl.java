package io.github.gms.secure.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import io.github.gms.common.entity.KeystoreEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.event.EntityDisabledEvent;
import io.github.gms.common.event.EntityDisabledEvent.EntityType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.KeystoreService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@CacheConfig(cacheNames = "keystoreCache")
public class KeystoreServiceImpl implements KeystoreService {

	private static final String SLASH = "/";

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeystoreRepository repository;
	
	@Autowired
	private KeystoreConverter converter;
	
	@Autowired
	private Gson gson;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Value("${config.location.keystore.path}")
	private String keystorePath;

	@Override
	@CacheEvict
	public SaveEntityResponseDto save(String model, MultipartFile file) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		
		SaveKeystoreRequestDto dto;
		try {
			dto = gson.fromJson(model, SaveKeystoreRequestDto.class);
		} catch (Exception e) {
			throw new GmsException(e);
		}

		dto.setUserId(userId);

		// Persist data
		KeystoreEntity entity;
		
		if (dto.getId() == null) {
			validateNewKeystore(dto, file);
			entity = converter.toNewEntity(dto, file);
		} else {
			Optional<KeystoreEntity> foundEntity = repository.findByIdAndUserId(dto.getId(), userId);
			
			if (foundEntity.isEmpty()) {
				throw new GmsException("Entity not found!");
			}
			
			entity = converter.toEntity(foundEntity.get(), dto, file);
		}

		try {
			byte[] fileContent;
			if (file == null) {
				File keystoreFile = new File(keystorePath + userId + SLASH + entity.getFileName());
				fileContent = Files.readAllBytes(keystoreFile.toPath());
			} else {
				fileContent = file.getBytes();
			}
			
			// Validate keystore file credentials
			cryptoService.validateKeyStoreFile(dto, fileContent);
		} catch (Exception e) {
			log.error("Keystore validation failed", e);
			throw new GmsException(e);
		}

		// Persist the keystore
		entity = repository.save(entity);
		
		if (EntityStatus.DISABLED == entity.getStatus()) {
			applicationEventPublisher.publishEvent(new EntityDisabledEvent(this, userId, entity.getId(), EntityType.KEYSTORE));
		}

		// Persist file
		if (file == null) {
			return new SaveEntityResponseDto(entity.getId());
		}

		try {
			Files.createDirectories(Paths.get(keystorePath + userId + SLASH));
			File keystoreFile = new File(keystorePath + userId + SLASH + file.getOriginalFilename());
		
			try (FileOutputStream outputStream = new FileOutputStream(keystoreFile)) {
			    outputStream.write(file.getBytes());
			}
		} catch (Exception e) {
			log.error("File cannot be copied", e);
			throw new GmsException(e);
		}
		
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	@CacheEvict(cacheNames = "keystoreCache", cacheManager = "keystoreCacheManager")
	public SaveEntityResponseDto save(SaveKeystoreRequestDto dto) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public KeystoreDto getById(Long id) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return converter.toDto(getKeystore(id, userId));
	}

	@Override
	public KeystoreListDto list(PagingDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));

		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		Pageable pagingRequest = PageRequest.of(dto.getPage(), dto.getSize(), sort);

		try {
			Page<KeystoreEntity> resultList = repository.findAllByUserId(userId, pagingRequest);
			return converter.toDtoList(resultList);
		} catch (Exception e) {
			return new KeystoreListDto(Collections.emptyList());
		}
	}

	@Override
	@CacheEvict
	public void delete(Long id) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		KeystoreEntity entity = getKeystore(id, userId);
		File keystoreFile = new File(keystorePath + entity.getUserId() + SLASH + entity.getFileName());
		
		repository.deleteById(id);

		try {
			log.info("Keystore file={} will be removed", keystoreFile.toPath().toString());
			Files.delete(keystoreFile.toPath());
		} catch (IOException e) {
			log.error("Keystore file cannot be deleted", e);
		}
	}

	@Override
	@CacheEvict
	public void toggleStatus(Long id, boolean enabled) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		KeystoreEntity entity = getKeystore(id, userId);
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
	}

	@Override
	public String getValue(GetSecureValueDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		KeystoreEntity entity = getKeystore(dto.getEntityId(), userId);
		return getValue(entity, dto.getValueType());
	}
	
	@Override
	public LongValueDto count() {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return new LongValueDto(repository.countByUserId(userId));
	}
	
	@Override
	public IdNamePairListDto getAllKeystoreNames() {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return new IdNamePairListDto(repository.getAllKeystoreNames(userId));
	}

	private void validateNewKeystore(SaveKeystoreRequestDto dto, MultipartFile file) {
		if (file == null) {
			throw new GmsException("Keystore file must be provided!");
		}
		
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		List<String> keystoreNames = repository.getAllKeystoreNames(userId, dto.getName());
		
		if (!keystoreNames.isEmpty()) {
			throw new GmsException("Keystore name must be unique!");
		}
	}
	
	private KeystoreEntity getKeystore(Long id, Long userId) {
		Optional<KeystoreEntity> entityOptional = repository.findByIdAndUserId(id, userId);
		
		if (entityOptional.isEmpty()) {
			log.warn(Constants.ENTITY_NOT_FOUND);
			throw new GmsException(Constants.ENTITY_NOT_FOUND);
		}
		
		return entityOptional.get();
	}

	private String getValue(KeystoreEntity entity, KeyStoreValueType valueType) {
		if (KeyStoreValueType.KEYSTORE_ALIAS == valueType) {
			return entity.getAlias();
		} else if (KeyStoreValueType.KEYSTORE_ALIAS_CREDENTIAL == valueType) {
			return entity.getAliasCredential();
		}

		return entity.getCredential();
	}
}
