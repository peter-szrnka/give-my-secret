package io.github.gms.functions.systemproperty;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.GmsException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static io.github.gms.common.util.Constants.CACHE_SYSTEM_PROPERTY;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CACHE_SYSTEM_PROPERTY)
public class SystemPropertyService {

	private final SystemPropertyConverter converter;
	private final SystemPropertyRepository repository;

	@CacheEvict(allEntries = true)
	public void save(SystemPropertyDto dto) {
		SystemPropertyEntity entity = repository.findByKey(getSystemPropertyByName(dto.getKey()));
		repository.save(converter.toEntity(entity, dto));
	}

	@CacheEvict(allEntries = true)
	public void delete(String key) {
		repository.deleteByKey(getSystemPropertyByName(key));
	}

	public SystemPropertyListDto list(Pageable pageable) {
		try {
			Page<SystemPropertyEntity> resultList = repository.findAll(pageable);
			return converter.toDtoList(resultList.getContent());
		} catch (Exception e) {
			return SystemPropertyListDto.builder().resultList(Collections.emptyList()).totalElements(0).build();
		}
	}

	@Cacheable
	public String get(SystemProperty property) {
		return repository.getValueByKey(property).orElse(property.getDefaultValue());
	}

	@Cacheable
	public Long getLong(SystemProperty property) {
		return Long.parseLong(get(property));
	}

	private SystemProperty getSystemPropertyByName(String key) {
		return SystemProperty.getByKey(key).orElseThrow(() -> new GmsException("Unknown system property!"));
	}

	public boolean getBoolean(SystemProperty key) {
		return Boolean.parseBoolean(get(key));
	}

	public Integer getInteger(SystemProperty key) {
		return Integer.parseInt(get(key));
	}
}