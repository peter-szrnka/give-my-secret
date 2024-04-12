package io.github.gms.functions.secret;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface ApiKeyRestrictionRepository extends JpaRepository<ApiKeyRestrictionEntity, Long> {

	List<ApiKeyRestrictionEntity> findAllByUserIdAndSecretId(Long userId, Long secretId);

	@Transactional
	void deleteByUserIdAndSecretIdAndApiKeyId(Long userId, Long secretId, Long apiKeyId);

	@Modifying
	@org.springframework.transaction.annotation.Transactional
	@Query("DELETE FROM ApiKeyRestrictionEntity a where a.userId in :userIds")
	void deleteAllByUserId(@Param("userIds") Set<Long> userIds);
}
