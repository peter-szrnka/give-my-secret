package io.github.gms.common.util;

import io.github.gms.secure.dto.PagingDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class ConverterUtils {

	private ConverterUtils() {}
	
	public static Pageable createPageable(PagingDto dto) {
		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		return PageRequest.of(dto.getPage(), dto.getSize(), sort);
	}
}
