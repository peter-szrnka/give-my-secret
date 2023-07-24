package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.secure.converter.SystemPropertyConverter;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;
import io.github.gms.secure.repository.SystemPropertyRepository;
import io.github.gms.secure.service.SystemPropertyService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@CacheConfig(cacheNames = "systemPropertyCache")
public class SystemPropertyServiceImpl implements SystemPropertyService {

	private final SystemPropertyConverter converter;
	private final SystemPropertyRepository repository;

	public SystemPropertyServiceImpl(SystemPropertyConverter converter, SystemPropertyRepository repository) {
		this.converter = converter;
		this.repository = repository;
	}

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
}