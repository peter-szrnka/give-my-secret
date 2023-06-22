package io.github.gms.secure.repository;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.dto.IdNamePairDto;
import io.github.gms.secure.entity.KeystoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreRepository extends JpaRepository<KeystoreEntity, Long> {
	
	Optional<KeystoreEntity> findByIdAndUserId(Long id, Long userId);
	
	Optional<KeystoreEntity> findByIdAndUserIdAndStatus(Long id, Long userId, EntityStatus status);
	
	Page<KeystoreEntity> findAllByUserId(Long userId, Pageable pagingRequest);
	
	long countByUserId(Long userId);

	@Query("select new io.github.gms.secure.dto.IdNamePairDto(k.id,k.name) from KeystoreEntity k where k.status='ACTIVE' and k.userId = :userId")
	List<IdNamePairDto> getAllKeystoreNames(@Param("userId") Long userId);
	
	@Query("select count(k) from KeystoreEntity k where k.userId = :userId and k.name = :name")
	long countAllKeystoresByName(@Param("userId") Long userId, @Param("name") String name);

	@Query("select k.fileName from KeystoreEntity k where k.fileName = :fileName")
	String findByFileName(@Param("fileName") String fileName);
}
