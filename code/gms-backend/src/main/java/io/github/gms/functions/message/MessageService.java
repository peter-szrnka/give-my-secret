package io.github.gms.functions.message;

import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import org.springframework.data.domain.Pageable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface MessageService extends BatchDeletionService {
	
	SaveEntityResponseDto save(MessageDto dto);

	MessageListDto list(Pageable pageable);
	
	long getUnreadMessagesCount();

	void toggleMarkAsRead(MarkAsReadRequestDto dto);

	void deleteById(Long id);

	void deleteAllByIds(IdListDto dto);
}
