package io.github.gms.functions.iprestriction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface IpRestrictionRepository extends JpaRepository<IpRestrictionEntity, Long> {

    @Query("SELECT io.github.gms.functions.secret.iprestriction.IpRestrictionPattern(i.ipPattern, i.allow) " +
            "from IpRestrictionEntity i where i.userId = :userId and i.secretId = :secretId")
    List<IpRestrictionPattern> getAllPatternData(@Param("userId") Long userId, @Param("secretId") Long secretId);
}
