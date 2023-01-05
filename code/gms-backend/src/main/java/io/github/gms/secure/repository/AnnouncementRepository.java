package io.github.gms.secure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.gms.secure.entity.AnnouncementEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {

}
