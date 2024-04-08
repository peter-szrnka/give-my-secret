package io.github.gms.functions.systemproperty;

import io.github.gms.common.enums.SystemProperty;
import org.springframework.data.domain.Pageable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemPropertyService {
	
	void save(SystemPropertyDto dto);
	
	void delete(String key);
	
	SystemPropertyListDto list(Pageable pageable);

	String get(SystemProperty key);
	
	Long getLong(SystemProperty key);

	boolean getBoolean(SystemProperty key);

	Integer getInteger(SystemProperty key);
}