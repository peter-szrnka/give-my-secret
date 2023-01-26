package io.github.gms.secure.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.gms.secure.entity.EventEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

	@Query("select e from EventEntity e where e.eventDate < :eventDate")
	List<EventEntity> findAllEventDateOlderThan(@Param("eventDate") ZonedDateTime eventDate);
}
