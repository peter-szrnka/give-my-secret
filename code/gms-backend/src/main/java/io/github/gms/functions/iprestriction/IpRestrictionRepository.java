package io.github.gms.functions.iprestriction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface IpRestrictionRepository extends JpaRepository<IpRestrictionEntity, Long> {

    List<IpRestrictionEntity> findAllBySecretId(Long secretId);
}
