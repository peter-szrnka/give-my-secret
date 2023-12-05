package io.github.gms.secure.repository;

import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface ApiKeyRestrictionRepository extends JpaRepository<ApiKeyRestrictionEntity, Long> {

	List<ApiKeyRestrictionEntity> findAllByUserIdAndSecretId(Long userId, Long secretId);

	@Transactional
	void deleteByUserIdAndSecretIdAndApiKeyId(Long userId, Long secretId, Long apiKeyId);
}
