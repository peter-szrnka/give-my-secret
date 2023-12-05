package io.github.gms.secure.repository;

import io.github.gms.secure.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface EventRepository extends JpaRepository<EventEntity, Long> {

	@Query("select e from EventEntity e where e.eventDate < :eventDate")
	List<EventEntity> findAllEventDateOlderThan(@Param("eventDate") ZonedDateTime eventDate);
	
	Page<EventEntity> findAllByUserId(Long userId, Pageable pageable);
}
