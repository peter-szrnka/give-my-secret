package io.github.gms.secure.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.secure.entity.SystemPropertyEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface SystemPropertyRepository extends JpaRepository<SystemPropertyEntity, Long> {

	@Transactional
	void deleteByKey(SystemProperty key);

	Optional<String> getValueByKey(SystemProperty key);

	SystemPropertyEntity findByKey(SystemProperty key);

}