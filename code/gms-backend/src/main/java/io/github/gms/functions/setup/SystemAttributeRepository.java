package io.github.gms.functions.setup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface SystemAttributeRepository extends JpaRepository<SystemAttributeEntity, String> {

    @Query("SELECT sa FROM SystemAttributeEntity sa WHERE sa.name = 'SYSTEM_STATUS'")
    Optional<SystemAttributeEntity> getSystemStatus();
}
