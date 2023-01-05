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
import io.github.gms.secure.entity.ApiKeyEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {

	ApiKeyEntity findByValueAndStatus(String value, EntityStatus status);
	
	Optional<ApiKeyEntity> findByIdAndUserId(Long id, Long userId);
	
	Page<ApiKeyEntity> findAllByUserId(Long userId, Pageable pageable);

	long countByUserId(Long userId);
	
	@Query("select new io.github.gms.secure.dto.IdNamePairDto(a.id,a.name) from ApiKeyEntity a where a.status='ACTIVE' and a.userId = :userId")
	List<IdNamePairDto> getAllApiKeyNames(@Param("userId") Long userId);
}
