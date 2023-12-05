package io.github.gms.secure.repository;

import io.github.gms.secure.entity.UserEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.github.gms.common.util.Constants.CACHE_USER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
@CacheConfig(cacheNames = CACHE_USER)
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsernameAndCredential(String username, String credential);
	
	@Query("SELECT COUNT(u) from UserEntity u where u.roles LIKE '%ADMIN%'")
	long countExistingAdmins();

	Optional<UserEntity> findByUsernameOrEmail(String username, String email);

	Optional<UserEntity> findByUsername(String username);

	@Query("SELECT COUNT(u) from UserEntity u where u.roles like '%ROLE_USER%' or u.roles like '%ROLE_VIEWER%'")
	long countNormalUsers();

	@Cacheable
	@Query("SELECT u.username from UserEntity u where u.id = :userId")
	String getUsernameById(@Param("userId") Long userId);
}
