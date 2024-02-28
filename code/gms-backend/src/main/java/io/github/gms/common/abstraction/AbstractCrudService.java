package io.github.gms.common.abstraction;

import io.github.gms.common.dto.PagingDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 * 
 * @param <A> DTO for save new entity
 * @param <B> The result object of the save process
 * @param <C> Object type that getById returns
 * @param <D> List object type
 */
public interface AbstractCrudService<A, B, C, D> extends GmsClientService {

	B save(A dto);

	C getById(Long id);

	D list(PagingDto dto);
}
