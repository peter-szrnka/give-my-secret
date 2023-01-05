package io.github.gms.secure.service.impl;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.converter.MessageConverter;
import io.github.gms.secure.dto.MarkAsReadRequestDto;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.secure.repository.MessageRepository;
import io.github.gms.secure.service.MessageService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class MessageServiceImpl implements MessageService {
	
	@Autowired
	private Clock clock;
	@Autowired
	private MessageRepository repository;
	@Autowired
	private MessageConverter converter;

	@Override
	public SaveEntityResponseDto save(MessageDto dto) {
		MessageEntity entity = repository.save(MessageEntity.builder()
				.userId(dto.getUserId())
				.opened(false)
				.message(dto.getMessage())
				.creationDate(LocalDateTime.now(clock))
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

		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		Pageable pagingRequest = PageRequest.of(dto.getPage(), dto.getSize(), sort);

		Page<MessageEntity> resultList = repository.findAllByUserId(userId, pagingRequest);
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
