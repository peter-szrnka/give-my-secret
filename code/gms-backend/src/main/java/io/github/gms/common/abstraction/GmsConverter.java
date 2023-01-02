package io.github.gms.common.abstraction;

import org.springframework.data.domain.Page;

/**
 * @author Peter Szrnka
 * @since 1.0
 * 
 * @param <L> Type of the list
 * @param <T> Type of the list item
 */
public interface GmsConverter<L, T> {
	
	L toDtoList(Page<T> resultList);
}
