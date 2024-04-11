package io.github.gms.functions.keystore;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.KeystoreBasicInfoDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.model.EntityChangeEvent;
import io.github.gms.common.model.EntityChangeEvent.EntityChangeType;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.service.FileService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.secret.GetSecureValueDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.gms.common.util.Constants.ALIAS_ID;
import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;
import static io.github.gms.common.util.Constants.KEYSTORE_ID;
import static io.github.gms.common.util.Constants.SLASH;
import static io.github.gms.common.util.Constants.USER_ID;
import static io.github.gms.common.util.FileUtils.validatePath;
import static io.github.gms.common.util.MdcUtils.getUserId;
import static java.util.Objects.requireNonNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { CACHE_API })
public class KeystoreServiceImpl implements KeystoreService {

	private final CryptoService cryptoService;
	private final KeystoreRepository repository;
	private final KeystoreAliasRepository aliasRepository;
	private final KeystoreConverter converter;
	private final ObjectMapper objectMapper;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final KeystoreFileService keystoreFileService;
	private final FileService fileService;
	@Setter
	@Value("${config.location.keystore.path}")
	private String keystorePath;
	@Setter
	@Value("${config.location.keystoreTemp.path}")
	private String keystoreTempPath;

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
	public KeystoreListDto list(Pageable pageable) {
		try {
			Page<KeystoreEntity> resultList = repository.findAllByUserId(getUserId(), pageable);
			return converter.toDtoList(resultList);
		} catch (Exception e) {
			return KeystoreListDto.builder().resultList(Collections.emptyList()).totalElements(0).build();
		}
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = { CACHE_API }, allEntries = true)
	public void delete(Long id) {
		KeystoreEntity entity = getKeystore(id);
		deleteFileById(id, entity.getUserId(), entity.getFileName(),true);

		aliasRepository.deleteByKeystoreId(id);
		repository.deleteById(id);
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
			return new DownloadFileResponseDto(entity.getFileName(), fileService.readAllBytes(Paths.get(getUserFolder() + entity.getFileName())));
		} catch (Exception e) {
			throw new GmsException(e);
		}
	}

	@Async
	@Override
	@Transactional
	public void batchDeleteByUserIds(Set<Long> userIds) {
		Set<KeystoreBasicInfoDto> keystoreIds = repository.findAllByUserId(userIds);

		keystoreIds.forEach(entity -> {
			deleteFileById(entity.getId(), entity.getUserId(), entity.getFilename(), false);
			aliasRepository.deleteByKeystoreId(entity.getId());
			repository.deleteById(entity.getId());
		});

		log.info("All keystore entities and files have been removed for the requested users");
	}

	private void deleteFileById(Long id, Long userId, String fileName, boolean publishEvent) {
		File keystoreFile = new File(keystorePath + userId + SLASH + fileName);

		try {
			log.info("Keystore file={} will be removed", keystoreFile.toPath());
			fileService.delete(keystoreFile.toPath());
			if (publishEvent) {
				publishEvent(initMetaData(id), EntityChangeType.KEYSTORE_DELETED);
			}
		} catch (IOException e) {
			log.error("Keystore file cannot be deleted", e);
		}
	}

	private void persistFile(KeystoreEntity newEntity, byte[] fileContent, boolean generated) {
		try {
			String newFileName = getUserFolder() + newEntity.getFileName();
			validatePath(newFileName);
			Path newFilePath = Paths.get(newFileName);

			if (fileService.exists(newFilePath)) {
				throw new GmsException("File name must be unique!");
			}

			fileService.createDirectories(Paths.get(getUserFolder()));
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
			metadata.put(ALIAS_ID, alias.getId());
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

        String tempFile = keystoreTempPath + filename;
        validatePath(tempFile);
		fileService.delete(Paths.get(tempFile));
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
		return repository.findById(id).orElseThrow(() -> {
			log.warn(ENTITY_NOT_FOUND);
			return new GmsException(ENTITY_NOT_FOUND);
		});
	}

	private String getAliasValue(GetSecureValueDto dto) {
		KeystoreAliasEntity entity = aliasRepository.findByIdAndKeystoreId(dto.getAliasId(), dto.getEntityId())
				.orElseThrow(() -> {
					log.warn(ENTITY_NOT_FOUND);
					return new GmsException(ENTITY_NOT_FOUND);
				});

		if (KeyStoreValueType.KEYSTORE_ALIAS == dto.getValueType()) {
			return entity.getAlias();
		}

		return entity.getAliasCredential();
	}

	private byte[] getFileContent(KeystoreEntity entity, MultipartFile file, SaveKeystoreRequestDto dto) {
		try {
			if (file != null) {
				validatePath(requireNonNull(file.getOriginalFilename()));
				return file.getBytes();
			}

			String folder = getUserFolder();
			String filename = entity.getFileName();

			if (dto.isGenerated()) {
				folder = keystoreTempPath;
				filename = keystoreFileService.generate(dto);
				entity.setFileName(filename);
			}

            validatePath(filename);
			Path keystoreFile = new File(folder + filename).toPath();

			if (!fileService.exists(keystoreFile)) {
				throw new GmsException("Keystore file does not exist!");
			}

			return fileService.readAllBytes(keystoreFile);
		} catch (GmsException e) {
			throw e;
		} catch (Exception e) {
			log.error("Keystore content cannot be parsed", e);
			throw new GmsException(e);
		}
	}

	private static Map<String, Object> initMetaData(Long keystoreId) {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put(USER_ID, getUserId());
		metadata.put(KEYSTORE_ID, keystoreId);
		return metadata;
	}
}