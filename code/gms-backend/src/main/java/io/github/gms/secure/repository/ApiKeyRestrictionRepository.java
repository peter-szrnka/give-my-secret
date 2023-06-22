package io.github.gms.secure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import jakarta.transaction.Transactional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ApiKeyRestrictionRepository extends JpaRepository<ApiKeyRestrictionEntity, Long> {

	List<ApiKeyRestrictionEntity> findAllByUserIdAndSecretId(Long userId, Long secretId);

	@Transactional
	void deleteByUserIdAndSecretIdAndApiKeyId(Long userId, Long secretId, Long apiKeyId);
}
