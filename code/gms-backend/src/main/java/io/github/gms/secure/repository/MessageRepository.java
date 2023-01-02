package io.github.gms.secure.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.gms.common.entity.MessageEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

	Optional<MessageEntity> findByIdAndUserId(Long id, Long userId);
	
	Page<MessageEntity> findAllByUserId(Long userId, Pageable pageable);

	long countByUserId(Long userId);
	
	@Query("select count(m) from MessageEntity m where m.opened = false and m.userId = :userId")
	Long countAllUnreadByUserId(@Param("userId") Long id);
	
	@Transactional
	@Modifying
	@Query("update MessageEntity m set m.opened=true where m.userId = :userId and m.id in :messageIds")
	void markAsRead(@Param("userId") Long userId, @Param("messageIds") Set<Long> messageIds);
}
