package io.github.gms.secure.service.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.converter.SystemPropertyConverter;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;
import io.github.gms.secure.repository.SystemPropertyRepository;
import io.github.gms.secure.service.SystemPropertyService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class SystemPropertyServiceImpl implements SystemPropertyService {
	
	@Autowired
	private SystemPropertyConverter converter;

	@Autowired
	private SystemPropertyRepository repository;

	@Override
	public void save(SystemPropertyDto dto) {
		SystemPropertyEntity entity = repository.findByKey(getSystemPropertyByName(dto.getKey()));
		repository.save(converter.toEntity(entity, dto));
	}

	@Override
	public void delete(String key) {
		repository.deleteByKey(getSystemPropertyByName(key));
	}

	@Override
	public SystemPropertyListDto list(PagingDto dto) {
		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());

		try {
			Page<SystemPropertyEntity> resultList = repository.findAll(PageRequest.of(dto.getPage(), dto.getSize(), sort));
			return converter.toDtoList(resultList.getContent());
		} catch (Exception e) {
			return new SystemPropertyListDto(Collections.emptyList());
		}
	}

	@Override
	public String get(String key) {
		SystemProperty property = getSystemPropertyByName(key);
		return repository.getValueByKey(property).orElse(property.getDefaultValue());
	}

	@Override
	public Long getLong(String key) {
		return Long.parseLong(get(key));
	}
	
	private SystemProperty getSystemPropertyByName(String key) {
		return SystemProperty.getByKey(key).orElseThrow(() -> new GmsException("Unknown system property!"));
	}
}