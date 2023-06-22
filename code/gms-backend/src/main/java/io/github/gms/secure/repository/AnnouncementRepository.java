package io.github.gms.secure.repository;

import io.github.gms.secure.entity.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {

}
