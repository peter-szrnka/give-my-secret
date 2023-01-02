package io.github.gms.secure.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.gms.common.entity.ApiKeyRestrictionEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface ApiKeyRestrictionRepository extends JpaRepository<ApiKeyRestrictionEntity, Long> {
	
	List<ApiKeyRestrictionEntity> findAllByUserIdAndSecretId(Long userId, Long secretId);

	//@Query("delete from ApiKeyRestrictionEntity a where a.userId = :userId and a.secretId = :secretId and a.apiKeyId = :apiKeyId")
	@Transactional
	void deleteByUserIdAndSecretIdAndApiKeyId(/*@Param("userId") */Long userId,/* @Param("secretId")*/ Long secretId,/* @Param("apiKeyId")*/ Long apiKeyId);
}
