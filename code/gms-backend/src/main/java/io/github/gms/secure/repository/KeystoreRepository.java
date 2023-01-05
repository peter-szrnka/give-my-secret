package io.github.gms.secure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.dto.IdNamePairDto;
import io.github.gms.secure.entity.KeystoreEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface KeystoreRepository extends JpaRepository<KeystoreEntity, Long> {
	
	Optional<KeystoreEntity> findByIdAndUserId(Long id, Long userId);
	
	Optional<KeystoreEntity> findByIdAndUserIdAndStatus(Long id, Long userId, EntityStatus status);
	
	Page<KeystoreEntity> findAllByUserId(Long userId, Pageable pagingRequest);
	
	long countByUserId(Long userId);

	@Query("select new io.github.gms.secure.dto.IdNamePairDto(k.id,k.name) from KeystoreEntity k where k.status='ACTIVE' and k.userId = :userId")
	List<IdNamePairDto> getAllKeystoreNames(@Param("userId") Long userId);
	
	@Query("select k.name from KeystoreEntity k where k.userId = :userId and name like %:name%")
	List<String> getAllKeystoreNames(@Param("userId") Long userId, @Param("name") String name);
}
