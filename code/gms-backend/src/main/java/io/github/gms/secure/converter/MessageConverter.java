package io.github.gms.secure.converter;

import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.entity.MessageEntity;
import org.springframework.data.domain.Page;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface MessageConverter {
	
	MessageDto toDto(MessageEntity entity);

	MessageListDto toDtoList(Page<MessageEntity> resultList);

}
