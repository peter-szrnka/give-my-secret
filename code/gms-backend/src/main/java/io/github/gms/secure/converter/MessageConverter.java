package io.github.gms.secure.converter;

import org.springframework.data.domain.Page;

import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.entity.MessageEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface MessageConverter {
	
	MessageDto toDto(MessageEntity entity);

	MessageListDto toDtoList(Page<MessageEntity> resultList);

}
