package io.github.gms.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
}
