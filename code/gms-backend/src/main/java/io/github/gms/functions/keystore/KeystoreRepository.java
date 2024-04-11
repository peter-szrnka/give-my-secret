package io.github.gms.functions.keystore;

import io.github.gms.common.dto.IdNamePairDto;
import io.github.gms.common.dto.KeystoreBasicInfoDto;
import io.github.gms.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.gms.common.util.Constants.USER_ID;

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

	@Query("select new io.github.gms.common.dto.IdNamePairDto(k.id,k.name) from KeystoreEntity k where k.status='ACTIVE' and k.userId = :userId")
	List<IdNamePairDto> getAllKeystoreNames(@Param(USER_ID) Long userId);
	
	@Query("select count(k) from KeystoreEntity k where k.userId = :userId and k.name = :name")
	long countAllKeystoresByName(@Param(USER_ID) Long userId, @Param("name") String name);

	@Query("select k.fileName from KeystoreEntity k where k.fileName = :fileName")
	String findByFileName(@Param("fileName") String fileName);

	@Query("select new io.github.gms.common.dto.KeystoreBasicInfoDto(k.id, k.userId, k.fileName) from KeystoreEntity k where k.userId in :userIds")
	Set<KeystoreBasicInfoDto> findAllByUserId(@Param("userIds") Set<Long> userIds);
}
