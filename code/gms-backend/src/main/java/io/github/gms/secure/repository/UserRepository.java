package io.github.gms.secure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.gms.secure.entity.UserEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsernameAndCredential(String username, String credential);
	
	@Query("SELECT COUNT(u) from UserEntity u where u.roles LIKE '%ADMIN%'")
	long countExistingAdmins();

	Optional<UserEntity> findByUsernameOrEmail(String username, String email);

	Optional<UserEntity> findByUsername(String username);

	@Query("SELECT COUNT(u) from UserEntity u where u.roles like '%ROLE_USER%' or u.roles like '%ROLE_VIEWER%'")
	long countNormalUsers();

	@Query("SELECT u.username from UserEntity u where u.id = :userId")
	String getUsernameById(@Param("userId") Long userId);
}
