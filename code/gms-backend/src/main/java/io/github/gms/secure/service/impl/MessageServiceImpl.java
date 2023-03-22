package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.secure.converter.MessageConverter;
import io.github.gms.secure.dto.*;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.secure.repository.MessageRepository;
import io.github.gms.secure.service.MessageService;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class MessageServiceImpl implements MessageService {

	private final Clock clock;
	private final MessageRepository repository;
	private final MessageConverter converter;

	public MessageServiceImpl(Clock clock, MessageRepository repository, MessageConverter converter) {
		this.clock = clock;
		this.repository = repository;
		this.converter = converter;
	}

	@Override
	public SaveEntityResponseDto save(MessageDto dto) {
		MessageEntity entity = repository.save(MessageEntity.builder()
				.userId(dto.getUserId())
				.opened(false)
				.message(dto.getMessage())
				.creationDate(ZonedDateTime.now(clock))
				.build());
		
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public MessageListDto list(PagingDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));

		Page<MessageEntity> resultList = repository.findAllByUserId(userId, ConverterUtils.createPageable(dto));
		return converter.toDtoList(resultList);
	}

	@Override
	public long getUnreadMessagesCount() {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		return repository.countAllUnreadByUserId(userId);
	}

	@Override
	public void markAsRead(MarkAsReadRequestDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		repository.markAsRead(userId, dto.getIds());
	}
}