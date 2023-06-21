package io.github.gms.secure.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.entity.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SecretRepository extends JpaRepository<SecretEntity, Long> {

	Optional<SecretEntity> findByUserIdAndSecretIdAndStatus(Long userId, String secretId, EntityStatus status);

	@Query("select s from SecretEntity s where (s.lastRotated is null or s.lastRotated <= ?1) and s.rotationEnabled = true and s.status=io.github.gms.common.enums.EntityStatus.ACTIVE") // 
	List<SecretEntity> findAllOldRotated(ZonedDateTime input);
	
	Optional<SecretEntity> findByIdAndUserId(Long id, Long userId);
	
	Page<SecretEntity> findAllByUserId(Long userId, Pageable pagingRequest);
	
	long countByUserId(Long userId);

	@Modifying
	@Query("update SecretEntity s set s.status=io.github.gms.common.enums.EntityStatus.DISABLED where s.status != io.github.gms.common.enums.EntityStatus.DISABLED and s.keystoreAliasId = :keystoreAliasId")
	void disableAllActiveByKeystoreAliasId(@Param("keystoreAliasId") Long keystoreAliasId);

	long countAllSecretsByUserIdAndSecretId(Long userId, String secretId);
}
