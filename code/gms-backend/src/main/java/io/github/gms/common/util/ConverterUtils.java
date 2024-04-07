package io.github.gms.common.util;

import io.github.gms.common.dto.PagingDto;
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

	public static Pageable createPageable(String direction, String property, int page, int size) {
		Sort sort = Sort.by(Direction.valueOf(direction), property);
		return PageRequest.of(page, size, sort);
	}
}
