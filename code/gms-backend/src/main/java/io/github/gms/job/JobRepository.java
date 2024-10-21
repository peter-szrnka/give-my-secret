package io.github.gms.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    @Query("select j from JobEntity j where j.endTime < :endTime") //
    List<JobEntity> findAllOld(@Param("endTime") ZonedDateTime endTime);
}
