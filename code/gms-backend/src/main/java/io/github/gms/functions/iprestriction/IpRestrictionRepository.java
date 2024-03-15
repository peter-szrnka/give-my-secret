package io.github.gms.functions.iprestriction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface IpRestrictionRepository extends JpaRepository<IpRestrictionEntity, Long> {

    @Query("select i from IpRestrictionEntity i where i.global=true")
    List<IpRestrictionEntity> findAllGlobal();

    @Query("select i from IpRestrictionEntity i where i.global=true")
    Page<IpRestrictionEntity> findAllGlobal(Pageable pageable);

    List<IpRestrictionEntity> findAllBySecretId(Long secretId);
}
