package io.github.gms.secure.service;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemPropertyService {
	
	void save(SystemPropertyDto dto);
	
	void delete(String key);
	
	SystemPropertyListDto list(PagingDto dto);

	String get(SystemProperty key);
	
	Long getLong(SystemProperty key);

	boolean getBoolean(SystemProperty key);

	Integer getInteger(SystemProperty key);
}