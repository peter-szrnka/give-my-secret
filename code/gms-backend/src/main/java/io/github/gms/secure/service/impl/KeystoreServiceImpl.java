package io.github.gms.secure.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.event.EntityChangeEvent;
import io.github.gms.common.event.EntityChangeEvent.EntityChangeType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.DownloadFileResponseDto;
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
import io.github.gms.secure.service.KeystoreFileService;
import io.github.gms.secure.service.KeystoreService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;
import static io.github.gms.common.util.Constants.SLASH;
import static io.github.gms.common.util.MdcUtils.getUserId;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = { CACHE_API })
public class KeystoreServiceImpl implements KeystoreService {

	private final CryptoService cryptoService;
	private final KeystoreRepository repository;
	private final KeystoreAliasRepository aliasRepository;
	private final KeystoreConverter converter;
	private final ObjectMapper objectMapper;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final KeystoreFileService keystoreFileService;
	@Setter
	@Value("${config.location.keystore.path}")
	private String keystorePath;
	@Setter
	@Value("${config.location.keystoreTemp.path}")
	private String keystoreTempPath;

	public KeystoreServiceImpl(
		CryptoService cryptoService,
		KeystoreRepository repository,
		KeystoreAliasRepository aliasRepository,
		KeystoreConverter converter,
		ObjectMapper objectMapper,
		ApplicationEventPublisher applicationEventPublisher,
		KeystoreFileService keystoreFileService
	) {
		this.cryptoService = cryptoService;
		this.repository = repository;
		this.aliasRepository = aliasRepository;
		this.converter = converter;
		this.objectMapper = objectMapper;
		this.applicationEventPublisher = applicationEventPublisher;
		this.keystoreFileService = keystoreFileService;
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public SaveEntityResponseDto save(String model, MultipartFile file) {
		SaveKeystoreRequestDto dto = parseInput(model);
		dto.setUserId(getUserId());
		
		// Validation
		validateInput(dto, file);
		
		// Prepare data to persist later
		KeystoreEntity entity = convertKeystore(dto, file);
		
		// Get file content
		byte[] fileContent = getFileContent(entity, file, dto);
		
		// Validate keystore file
		cryptoService.validateKeyStoreFile(dto, fileContent);

		// Persist the keystore
		final KeystoreEntity newEntity = repository.save(entity);
		
		// Process aliases
		dto.getAliases().forEach(alias -> processAlias(newEntity, alias));
		
		if (EntityStatus.DISABLED == newEntity.getStatus()) {
			publishEvent(initMetaData(newEntity.getId()), EntityChangeType.KEYSTORE_DISABLED);
		}

		if (dto.getId() == null) {
			// Persist file
			persistFile(newEntity, fileContent, dto.isGenerated());
		}
		
		return new SaveEntityResponseDto(newEntity.getId());
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
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
		try {
			Page<KeystoreEntity> resultList = repository.findAllByUserId(getUserId(), ConverterUtils.createPageable(dto));
			return converter.toDtoList(resultList);
		} catch (Exception e) {
			return new KeystoreListDto(Collections.emptyList());
		}
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void delete(Long id) {
		KeystoreEntity entity = getKeystore(id);
		File keystoreFile = new File(keystorePath + entity.getUserId() + SLASH + entity.getFileName());

		aliasRepository.deleteByKeystoreId(id);
		repository.deleteById(id);

		try {
			log.info("Keystore file={} will be removed", keystoreFile.toPath());
			Files.delete(keystoreFile.toPath());
			publishEvent(initMetaData(id), EntityChangeType.KEYSTORE_DELETED);
		} catch (IOException e) {
			log.error("Keystore file cannot be deleted", e);
		}
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void toggleStatus(Long id, boolean enabled) {
		KeystoreEntity entity = getKeystore(id);
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
		
		if (EntityStatus.DISABLED != entity.getStatus()) {
			return;
		}

		publishEvent(initMetaData(entity.getId()), EntityChangeType.KEYSTORE_DISABLED);
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
		return new LongValueDto(repository.countByUserId(getUserId()));
	}
	
	@Override
	public IdNamePairListDto getAllKeystoreNames() {
		return new IdNamePairListDto(repository.getAllKeystoreNames(getUserId()));
	}
	
	@Override
	public IdNamePairListDto getAllKeystoreAliasNames(Long keystoreId) {
		// We query the keystore entity just for validation purposes
		getKeystore(keystoreId);
		
		return new IdNamePairListDto(aliasRepository.getAllAliasNames(keystoreId));
	}

	@Override
	public DownloadFileResponseDto downloadKeystore(Long keystoreId) {
		KeystoreEntity entity = getKeystore(keystoreId);

		try {
			return new DownloadFileResponseDto(entity.getFileName(), Files.readAllBytes(Paths.get(getUserFolder() + entity.getFileName())));
		} catch (Exception e) {
			throw new GmsException(e);
		}
	}

	private void persistFile(KeystoreEntity newEntity, byte[] fileContent, boolean generated) {
		try {
			String newFileName = getUserFolder() + newEntity.getFileName();

			if (Files.exists(Paths.get(newFileName))) {
				throw new GmsException("File name must be unique!");
			}

			Files.createDirectories(Paths.get(getUserFolder()));
			File keystoreFile = new File(newFileName);

			FileOutputStream outputStream = new FileOutputStream(keystoreFile);
			outputStream.write(fileContent);
			outputStream.close();
			
			removeGeneratedFileFromTempFolder(newEntity.getFileName(), generated);
		} catch (GmsException e) {
			throw e;
		} catch (Exception e) {
			log.error("File cannot be copied", e);
			throw new GmsException(e);
		}
	}
	
	private String getUserFolder() {
		return keystorePath + getUserId() + SLASH;
	}
	
	private KeystoreEntity convertKeystore(SaveKeystoreRequestDto dto, MultipartFile file) {
		if (dto.getId() == null) {
			return converter.toNewEntity(dto, file);
		}
			
		KeystoreEntity foundEntity = repository.findByIdAndUserId(dto.getId(), getUserId())
				.orElseThrow(() -> new GmsException("Entity not found!"));

		return converter.toEntity(foundEntity, dto);
	}

	private void processAlias(KeystoreEntity newEntity, KeystoreAliasDto alias) {
		if (AliasOperation.SAVE == alias.getOperation()) {
			aliasRepository.save(converter.toAliasEntity(newEntity.getId(), alias));
		} else {
			aliasRepository.deleteById(alias.getId());
			Map<String, Object> metadata = initMetaData(newEntity.getId());
			metadata.put("aliasId", alias.getId());
			publishEvent(metadata, EntityChangeType.KEYSTORE_ALIAS_REMOVED);
		}
	}

	private void publishEvent(Map<String, Object> metadata, EntityChangeType entityChangeType) {
		applicationEventPublisher.publishEvent(new EntityChangeEvent(this, metadata, entityChangeType));
	}

	private void removeGeneratedFileFromTempFolder(String filename, boolean generated) throws IOException {
		if (!generated) {
			return;
		}

		Files.delete(Paths.get(keystoreTempPath + filename));
	}

	private SaveKeystoreRequestDto parseInput(String model) {
		try {
			return objectMapper.readValue(model, SaveKeystoreRequestDto.class);
		} catch (Exception e) {
			throw new GmsException(e);
		}
	}

	private void validateInput(SaveKeystoreRequestDto dto, MultipartFile file) {
		if (dto.getAliases().stream().noneMatch(alias -> AliasOperation.DELETE != alias.getOperation())) {
			throw new GmsException("You must define at least one keystore alias!");
		}
		
		if (file != null && dto.isGenerated()) {
			// Edge case: User cannot upload a keystore along with a generated keystore, only one can be selected
			throw new GmsException("Only one keystore source is allowed!");
		}

		validateKeystore(dto, file, dto.getId() == null ? 0 : 1);
	}

	private void validateKeystore(SaveKeystoreRequestDto dto, MultipartFile file, int expectedCount) {
		if (dto.isGenerated()) {
			return;
		}

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
			log.warn(ENTITY_NOT_FOUND);
			throw new GmsException(ENTITY_NOT_FOUND);
		});
	}

	private String getAliasValue(GetSecureValueDto dto) {
		KeystoreAliasEntity entity = aliasRepository.findByIdAndKeystoreId(dto.getAliasId(), dto.getEntityId())
			.orElseThrow(() -> {
				log.warn(ENTITY_NOT_FOUND);
				throw new GmsException(ENTITY_NOT_FOUND);
		});

		if (KeyStoreValueType.KEYSTORE_ALIAS == dto.getValueType()) {
			return entity.getAlias();
		}

		return entity.getAliasCredential();
	}

	private byte[] getFileContent(KeystoreEntity entity, MultipartFile file, SaveKeystoreRequestDto dto) {
		try {
			if (file != null) {
				return file.getBytes();
			}

			String folder = getUserFolder();
			String filename = entity.getFileName();
				
			if (dto.isGenerated()) {
				folder = keystoreTempPath;
				filename = keystoreFileService.generate(dto);
				entity.setFileName(filename);
			}
				
			File keystoreFile = new File(folder + filename);
			
			if (!Files.exists(keystoreFile.toPath())) {
				throw new GmsException("Keystore file does not exist!");
			}

			return Files.readAllBytes(keystoreFile.toPath());
		} catch (GmsException e) {
			throw e;
		} catch (Exception e) {
			log.error("Keystore content cannot be parsed", e);
			throw new GmsException(e);
		}
	}
	
	private static Map<String, Object> initMetaData(Long keystoreId) {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("userId", getUserId());
		metadata.put("keystoreId", keystoreId);
		return metadata;
	}
}