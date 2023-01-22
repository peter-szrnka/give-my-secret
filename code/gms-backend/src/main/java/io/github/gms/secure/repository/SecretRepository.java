package io.github.gms.secure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.entity.SecretEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface SecretRepository extends JpaRepository<SecretEntity, Long> {

	SecretEntity findByUserIdAndSecretIdAndStatus(Long userId, String secretId, EntityStatus status);

	@Query("select s from SecretEntity s where (s.lastRotated is null or s.lastRotated <= ?1) and s.rotationEnabled = true and s.status='ACTIVE'") // 
	List<SecretEntity> findAllOldRotated(LocalDateTime input);
	
	Optional<SecretEntity> findByIdAndUserId(Long id, Long userId);
	
	Page<SecretEntity> findAllByUserId(Long userId, Pageable pagingRequest);
	
	long countByUserId(Long userId);

	@Modifying
	@Query("update SecretEntity s set s.status='DISABLED' where s.status != 'DISABLED' and s.keystoreAliasId = :keystoreAliasId")
	void disableAllActiveByKeystoreAliasId(@Param("keystoreAliasId") Long keystoreAliasId);

	long countAllSecretsByUserIdAndSecretId(Long userId, String secretId);
}
