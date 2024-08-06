package io.github.gms.functions.message;

import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.MdcParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final Clock clock;
	private final MessageRepository repository;
	private final MessageConverter converter;

	@Override
	public SaveEntityResponseDto save(MessageDto dto) {
		MessageEntity entity = repository.save(MessageEntity.builder()
				.userId(dto.getUserId())
				.opened(false)
				.message(dto.getMessage())
				.creationDate(ZonedDateTime.now(clock))
				.actionPath(dto.getActionPath())
				.build());
		
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public MessageListDto list(Pageable pageable) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));

		Page<MessageEntity> resultList = repository.findAllByUserId(userId, pageable);
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

	@Async
	@Override
	public void batchDeleteByUserIds(Set<Long> userIds) {
		repository.deleteAllByUserId(userIds);
		log.info("All messages have been removed for the requested users");
	}
}