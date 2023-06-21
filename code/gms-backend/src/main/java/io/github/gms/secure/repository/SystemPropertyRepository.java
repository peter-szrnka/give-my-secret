package io.github.gms.secure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.secure.entity.SystemPropertyEntity;
import jakarta.transaction.Transactional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface SystemPropertyRepository extends JpaRepository<SystemPropertyEntity, Long> {

	@Transactional
	void deleteByKey(SystemProperty key);

	@Query("select s.value from SystemPropertyEntity s where s.key = :key")
	Optional<String> getValueByKey(@Param("key") SystemProperty key);

	SystemPropertyEntity findByKey(SystemProperty key);

}