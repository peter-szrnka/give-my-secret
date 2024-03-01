package io.github.gms.functions.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

	@Modifying
	@Transactional
	@Query("delete from EventEntity e where e.eventDate < :eventDate")
	int deleteAllEventDateOlderThan(@Param("eventDate") ZonedDateTime eventDate);
	
	Page<EventEntity> findAllByUserId(Long userId, Pageable pageable);
}
