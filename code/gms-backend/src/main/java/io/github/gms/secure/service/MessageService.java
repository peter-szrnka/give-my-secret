package io.github.gms.secure.service;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.secure.dto.MarkAsReadRequestDto;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;

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
