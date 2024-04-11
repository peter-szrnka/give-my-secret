package io.github.gms.functions.iprestriction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface IpRestrictionRepository extends JpaRepository<IpRestrictionEntity, Long> {

    @Query("select i from IpRestrictionEntity i where i.global=true and i.status='ACTIVE'")
    List<IpRestrictionEntity> findAllGlobal();

    @Query("select i from IpRestrictionEntity i where i.global=true")
    Page<IpRestrictionEntity> findAllGlobal(Pageable pageable);

    List<IpRestrictionEntity> findAllBySecretId(Long secretId);

    @Modifying
    @Transactional
    @Query("DELETE FROM IpRestrictionEntity i where i.userId in :userIds")
    void deleteAllByUserId(@Param("userIds") Set<Long> userIds);
}
