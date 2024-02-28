package io.github.gms.functions.message;

import io.github.gms.functions.message.MessageDto;
import io.github.gms.functions.message.MessageListDto;
import io.github.gms.functions.message.MessageEntity;
import org.springframework.data.domain.Page;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface MessageConverter {
	
	MessageDto toDto(MessageEntity entity);

	MessageListDto toDtoList(Page<MessageEntity> resultList);

}
