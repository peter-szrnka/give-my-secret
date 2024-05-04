package io.github.gms.functions.apikey;

import io.github.gms.common.abstraction.CountableRepository;
import io.github.gms.common.dto.IdNamePairDto;
import io.github.gms.common.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.gms.common.util.Constants.USER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface ApiKeyRepository extends CountableRepository<ApiKeyEntity, Long> {

	ApiKeyEntity findByValueAndStatus(String value, EntityStatus status);
	
	Optional<ApiKeyEntity> findByIdAndUserId(Long id, Long userId);
	
	Page<ApiKeyEntity> findAllByUserId(Long userId, Pageable pageable);
	
	@Query("select new io.github.gms.common.dto.IdNamePairDto(a.id,a.name) from ApiKeyEntity a where a.status='ACTIVE' and a.userId = :userId")
	List<IdNamePairDto> getAllApiKeyNames(@Param(USER_ID) Long userId);
	
	@Query("select count(a) from ApiKeyEntity a where a.userId = :userId and a.name = :name")
	long countAllApiKeysByName(@Param(USER_ID) Long userId, @Param("name") String name);
	
	@Query("select count(a) from ApiKeyEntity a where a.userId = :userId and a.value = :value")
	long countAllApiKeysByValue(@Param(USER_ID) Long userId, @Param("value") String value);

	@Modifying
	@Transactional
	@Query("DELETE FROM ApiKeyEntity a where a.userId in :userIds")
	void deleteAllByUserId(@Param("userIds") Set<Long> userIds);
}
