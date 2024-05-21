package io.github.gms.common.abstraction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Peter Szrnka
 * @since 1.0
 *
 * @param <T> Entity type
 * @param <ID> Type of the identifier
 */
@NoRepositoryBean
public interface CountableRepository<T, ID> extends JpaRepository<T, ID> {

    long countByUserId(Long userId);
}
