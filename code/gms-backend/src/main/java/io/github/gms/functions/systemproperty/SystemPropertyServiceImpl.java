package io.github.gms.functions.systemproperty;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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
public class SystemPropertyServiceImpl implements SystemPropertyService {

	private final SystemPropertyConverter converter;
	private final SystemPropertyRepository repository;

	@Override
	@CacheEvict(allEntries = true)
	public void save(SystemPropertyDto dto) {
		SystemPropertyEntity entity = repository.findByKey(getSystemPropertyByName(dto.getKey()));
		repository.save(converter.toEntity(entity, dto));
	}

	@Override
	@CacheEvict(allEntries = true)
	public void delete(String key) {
		repository.deleteByKey(getSystemPropertyByName(key));
	}

	@Override
	public SystemPropertyListDto list(PagingDto dto) {
		try {
			Page<SystemPropertyEntity> resultList = repository.findAll(ConverterUtils.createPageable(dto));
			return converter.toDtoList(resultList.getContent());
		} catch (Exception e) {
			return SystemPropertyListDto.builder().resultList(Collections.emptyList()).totalElements(0).build();
		}
	}

	@Override
	@Cacheable
	public String get(SystemProperty property) {
		return repository.getValueByKey(property).orElse(property.getDefaultValue());
	}

	@Override
	@Cacheable
	public Long getLong(SystemProperty property) {
		return Long.parseLong(get(property));
	}
	
	private SystemProperty getSystemPropertyByName(String key) {
		return SystemProperty.getByKey(key).orElseThrow(() -> new GmsException("Unknown system property!"));
	}

	@Override
	public boolean getBoolean(SystemProperty key) {
		return Boolean.parseBoolean(get(key));
	}

	@Override
	public Integer getInteger(SystemProperty key) {
		return Integer.parseInt(get(key));
	}
}