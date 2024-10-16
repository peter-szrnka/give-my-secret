package io.github.gms.functions.user;

import io.github.gms.common.enums.EntityStatus;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.gms.common.util.Constants.CACHE_USER;
import static io.github.gms.common.util.Constants.USER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
@CacheConfig(cacheNames = CACHE_USER)
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	@Query("SELECT COUNT(u) from UserEntity u where u.role = 'ROLE_ADMIN'")
	long countExistingAdmins();

	@Query("SELECT u from UserEntity u where u.role = 'ROLE_ADMIN'")
	List<UserEntity> getAllAdmins();

	Optional<UserEntity> findByUsernameOrEmail(String username, String email);

	Optional<UserEntity> findByUsername(String username);

	@Query("SELECT COUNT(u) from UserEntity u where u.role = 'ROLE_USER' or u.role = 'ROLE_VIEWER'")
	long countNormalUsers();

	@Cacheable
	@Query("SELECT u.username from UserEntity u where u.id = :userId")
	String getUsernameById(@Param(USER_ID) Long userId);

	@Query("SELECT u.id from UserEntity u where u.username = :username")
	Optional<Long> getIdByUsername(@Param("username") String username);

	@Query("SELECT u.username from UserEntity u where u.status!='TO_BE_DELETED'")
	List<String> getAllUserNames();

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u set u.status='TO_BE_DELETED' where u.username = :username and u.status!='TO_BE_DELETED'")
	void markUserAsDeleted(@Param("username") String username);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u set u.status=:newStatus where u.id in :userIds")
    void batchUpdateStatus(@Param("userIds") Set<Long> userIds, @Param("newStatus") EntityStatus newStatus);

	@Modifying
	@Transactional
	@Query("DELETE FROM UserEntity u where u.id in :userIds")
	void deleteAllByUserId(@Param("userIds") Set<Long> userIds);

	@Query("SELECT u.id from UserEntity u where u.status=:status")
	Set<Long> findAllByStatus(@Param("status") EntityStatus status);

	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u set u.name='**',u.username='**',u.credential='**',u.email='**',u.status='ANONYMIZED' where u.id in :userIds")
    void batchUpdateUserPersonalInfo(@Param("userIds") Set<Long> userIds);
}
