package io.github.gms.secure.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.event.EntityChangeEvent;
import io.github.gms.common.event.EntityChangeEvent.EntityChangeType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreAliasDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.secure.service.KeystoreService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeystoreServiceImpl implements KeystoreService {

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private KeystoreRepository repository;
	
	@Autowired
	private KeystoreAliasRepository aliasRepository;
	
	@Autowired
	private KeystoreConverter converter;
	
	@Autowired
	private Gson gson;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Value("${config.location.keystore.path}")
	private String keystorePath;

	@Override
	public SaveEntityResponseDto save(String model, MultipartFile file) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		
		SaveKeystoreRequestDto dto;
		try {
			dto = gson.fromJson(model, SaveKeystoreRequestDto.class);
		} catch (Exception e) {
			throw new GmsException(e);
		}
		
		dto.setUserId(userId);
		
		// Validation
		validateInput(dto, file);

		// Persist data
		KeystoreEntity entity = convertKeystore(dto, file);

		// Process and validate file content
		try {
			byte[] fileContent;
			if (file == null) {
				File keystoreFile = new File(keystorePath + userId + Constants.SLASH + entity.getFileName());
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
		final KeystoreEntity newEntity = repository.save(entity);
		
		// Process aliases
		dto.getAliases().forEach(alias -> processAlias(newEntity, alias));
		
		if (EntityStatus.DISABLED == newEntity.getStatus()) {
			Map<String, Object> metadata = initMetaData(userId, newEntity.getId());
			applicationEventPublisher.publishEvent(new EntityChangeEvent(this, metadata, EntityChangeType.KEYSTORE_DISABLED));
		}

		// Persist file
		if (file == null) {
			return new SaveEntityResponseDto(newEntity.getId());
		}

		try {
			String newFileName = keystorePath + userId + Constants.SLASH + file.getOriginalFilename();
			
			if (Files.exists(Paths.get(newFileName))) {
				throw new GmsException("File name must be unique!");
			}
			
			Files.createDirectories(Paths.get(keystorePath + userId + Constants.SLASH));
			File keystoreFile = new File(newFileName);
		
			FileOutputStream outputStream = new FileOutputStream(keystoreFile);
			outputStream.write(file.getBytes());
			outputStream.close();
		} catch (GmsException e) {
			throw e;
		} catch (Exception e) {
			log.error("File cannot be copied", e);
			throw new GmsException(e);
		}
		
		return new SaveEntityResponseDto(newEntity.getId());
	}

	@Override
	public SaveEntityResponseDto save(SaveKeystoreRequestDto dto) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public KeystoreDto getById(Long id) {
		KeystoreEntity entity = getKeystore(id);
		List<KeystoreAliasEntity> aliases = aliasRepository.findAllByKeystoreId(id);

		return converter.toDto(entity, aliases);
	}

	@Override
	public KeystoreListDto list(PagingDto dto) {
		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		Pageable pagingRequest = PageRequest.of(dto.getPage(), dto.getSize(), sort);

		try {
			Page<KeystoreEntity> resultList = repository.findAllByUserId(getUserId(), pagingRequest);
			return converter.toDtoList(resultList);
		} catch (Exception e) {
			return new KeystoreListDto(Collections.emptyList());
		}
	}

	@Override
	@Transactional
	public void delete(Long id) {
		KeystoreEntity entity = getKeystore(id);
		File keystoreFile = new File(keystorePath + entity.getUserId() + Constants.SLASH + entity.getFileName());

		aliasRepository.deleteByKeystoreId(id);
		repository.deleteById(id);

		try {
			log.info("Keystore file={} will be removed", keystoreFile.toPath().toString());
			Files.delete(keystoreFile.toPath());
		} catch (IOException e) {
			log.error("Keystore file cannot be deleted", e);
		}
	}

	@Override
	public void toggleStatus(Long id, boolean enabled) {
		KeystoreEntity entity = getKeystore(id);
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
		
		if (EntityStatus.DISABLED != entity.getStatus()) {
			return;
		}

		Map<String, Object> metadata = initMetaData(getUserId(), entity.getId());
		applicationEventPublisher.publishEvent(new EntityChangeEvent(this, metadata, EntityChangeType.KEYSTORE_DISABLED));
	}

	@Override
	public String getValue(GetSecureValueDto dto) {
		KeystoreEntity entity = getKeystore(dto.getEntityId());

		if (KeyStoreValueType.KEYSTORE_CREDENTIAL == dto.getValueType()) {
			return entity.getCredential();
		}
		
		return getAliasValue(dto);
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
	
	@Override
	public IdNamePairListDto getAllKeystoreAliasNames(Long keystoreId) {
		// We query the keystore entity just for validation purposes
		getKeystore(keystoreId);
		
		return new IdNamePairListDto(aliasRepository.getAllAliasNames(keystoreId));
	}
	
	private Long getUserId() {
		return Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
	}
	
	private KeystoreEntity convertKeystore(SaveKeystoreRequestDto dto, MultipartFile file) {
		if (dto.getId() == null) {
			return converter.toNewEntity(dto, file);
		}
			
		KeystoreEntity foundEntity = repository.findByIdAndUserId(dto.getId(), getUserId())
				.orElseThrow(() -> new GmsException("Entity not found!"));

		return converter.toEntity(foundEntity, dto, file);
	}

	private void processAlias(KeystoreEntity newEntity, KeystoreAliasDto alias) {
		if (AliasOperation.SAVE == alias.getOperation()) {
			aliasRepository.save(converter.toAliasEntity(newEntity.getId(), alias));
		} else {
			aliasRepository.deleteById(alias.getId());
			Map<String, Object> metadata = initMetaData(getUserId(), newEntity.getId());
			metadata.put("aliasId", alias.getId());
			applicationEventPublisher.publishEvent(new EntityChangeEvent(this, metadata, EntityChangeType.KEYSTORE_ALIAS_REMOVED));
		}
	}	

	private void validateInput(SaveKeystoreRequestDto dto, MultipartFile file) {
		if (dto.getAliases().stream().filter(alias -> AliasOperation.DELETE != alias.getOperation()).count() == 0) {
			throw new GmsException("You must define at least one keystore alias!");
		}

		validateKeystore(dto, file, dto.getId() == null ? 0 : 1);
	}

	private void validateKeystore(SaveKeystoreRequestDto dto, MultipartFile file, int expectedCount) {
		if (dto.getId() == null && file == null) {
			throw new GmsException("Keystore file must be provided!");
		}

		long keystoreCount = repository.countAllKeystoresByName(getUserId(), dto.getName());

		if (keystoreCount > expectedCount) {
			throw new GmsException("Keystore name must be unique!");
		}
	}
	
	private KeystoreEntity getKeystore(Long id) {
		return repository.findByIdAndUserId(id, getUserId()).orElseThrow(() -> {
			log.warn(Constants.ENTITY_NOT_FOUND);
			throw new GmsException(Constants.ENTITY_NOT_FOUND);
		});
	}

	private String getAliasValue(GetSecureValueDto dto) {
		KeystoreAliasEntity entity = aliasRepository.findByIdAndKeystoreId(dto.getAliasId(), dto.getEntityId())
			.orElseThrow(() -> {
				log.warn(Constants.ENTITY_NOT_FOUND);
				throw new GmsException(Constants.ENTITY_NOT_FOUND);
		});

		if (KeyStoreValueType.KEYSTORE_ALIAS == dto.getValueType()) {
			return entity.getAlias();
		}

		return entity.getAliasCredential();
	}
	
	private static Map<String, Object> initMetaData(Long userId, Long keystoreId) {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("userId", userId);
		metadata.put("keystoreId", keystoreId);
		return metadata;
	}
}