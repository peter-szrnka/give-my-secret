package io.github.gms.functions.message;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface MessageService extends GmsService {
	
	SaveEntityResponseDto save(MessageDto dto);

	MessageListDto list(PagingDto dto);
	
	long getUnreadMessagesCount();

	void markAsRead(MarkAsReadRequestDto dto);
}
