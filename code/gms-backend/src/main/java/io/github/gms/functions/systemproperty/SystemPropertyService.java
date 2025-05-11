package io.github.gms.functions.systemproperty;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.ErrorCode;
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
		SystemProperty systemProperty = getSystemPropertyByName(dto.getKey());
		SystemPropertyEntity entity = repository.findByKey(systemProperty);

		if (!systemProperty.getValidator().validate(dto.getValue())) {
			throw new GmsException("Invalid value for system property!", ErrorCode.GMS_027);
		}

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
		return getValueByKey(property);
	}

	@Cacheable
	public Long getLong(SystemProperty property) {
		return Long.parseLong(getValueByKey(property));
	}

	public boolean getBoolean(SystemProperty key) {
		return Boolean.parseBoolean(getValueByKey(key));
	}

	public Integer getInteger(SystemProperty key) {
		return Integer.parseInt(getValueByKey(key));
	}

	public void updateSystemProperty(SystemPropertyDto systemPropertyDto) {
		SystemProperty systemProperty = getSystemPropertyByName(systemPropertyDto.getKey());
		SystemPropertyEntity entity = repository.findByKey(systemProperty);

		if (!systemProperty.getValidator().validate(systemPropertyDto.getValue())) {
			throw new GmsException("Invalid value for system property!", ErrorCode.GMS_027);
		}

		repository.save(converter.toEntity(entity, systemPropertyDto));
	}

	private String getValueByKey(SystemProperty property) {
		return repository.getValueByKey(property).orElse(property.getDefaultValue());
	}

	private SystemProperty getSystemPropertyByName(String key) {
		return SystemProperty.getByKey(key).orElseThrow(() -> new GmsException("Unknown system property!", ErrorCode.GMS_026));
	}
}