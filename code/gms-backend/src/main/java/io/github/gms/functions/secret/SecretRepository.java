package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.CountableRepository;
import io.github.gms.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface SecretRepository extends CountableRepository<SecretEntity, Long> {

	Optional<SecretEntity> findByUserIdAndSecretIdAndStatus(Long userId, String secretId, EntityStatus status);

	@Query("select s from SecretEntity s where (s.lastRotated is null or s.lastRotated <= ?1) and s.rotationEnabled = true and s.status=io.github.gms.common.enums.EntityStatus.ACTIVE") // 
	List<SecretEntity> findAllOldRotated(ZonedDateTime input);
	
	Optional<SecretEntity> findByIdAndUserId(Long id, Long userId);
	
	Page<SecretEntity> findAllByUserId(Long userId, Pageable pagingRequest);

	@Modifying
	@Query("update SecretEntity s set s.status=io.github.gms.common.enums.EntityStatus.DISABLED where s.status != io.github.gms.common.enums.EntityStatus.DISABLED and s.keystoreAliasId = :keystoreAliasId")
	void disableAllActiveByKeystoreAliasId(@Param("keystoreAliasId") Long keystoreAliasId);

	long countAllSecretsByUserIdAndSecretId(Long userId, String secretId);

	@Modifying
	@Transactional
	@Query("DELETE FROM SecretEntity s where s.userId in :userIds")
	void deleteAllByUserId(@Param("userIds") Set<Long> userIds);
}
