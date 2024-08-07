package io.github.gms.functions.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Set;

import static io.github.gms.common.util.Constants.USER_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
	
	Page<MessageEntity> findAllByUserId(Long userId, Pageable pageable);
	
	@Query("select count(m) from MessageEntity m where m.opened = false and m.userId = :userId")
	Long countAllUnreadByUserId(@Param(USER_ID) Long id);

	@Modifying
	@Transactional
	@Query("update MessageEntity m set m.opened=true where m.userId = :userId and m.id in :messageIds")
	void markAsRead(@Param(USER_ID) Long userId, @Param("messageIds") Set<Long> messageIds);

	@Modifying
	@Transactional
	@Query("delete from MessageEntity m where m.creationDate < :eventDate")
	int deleteAllEventDateOlderThan(@Param("eventDate") ZonedDateTime eventDate);

	@Modifying
	@Transactional
	@Query("DELETE FROM MessageEntity m where m.userId in :userIds")
	void deleteAllByUserId(@Param("userIds") Set<Long> userIds);

	@Modifying
	@Transactional
	@Query("DELETE FROM MessageEntity m where m.userId = :userId and m.id in :ids")
    void deleteAllByUserIdAndIds(@Param("userId") Long userId, @Param("ids") Set<Long> ids);
}
