package io.github.gms.functions.systemproperty;

import io.github.gms.common.enums.SystemProperty;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.github.gms.common.util.Constants.KEY;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface SystemPropertyRepository extends JpaRepository<SystemPropertyEntity, Long> {

	@Transactional
	void deleteByKey(SystemProperty key);

	@Query("select s.value from SystemPropertyEntity s where s.key = :key")
	Optional<String> getValueByKey(@Param(KEY) SystemProperty key);

	SystemPropertyEntity findByKey(SystemProperty key);

}