package io.github.gms.functions.message;

import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Set;

import static io.github.gms.common.util.MdcUtils.getUserId;

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
	public MessageListDto list(Pageable pageable) {
		Page<MessageEntity> resultList = repository.findAllByUserId(getUserId(), pageable);
		return converter.toDtoList(resultList);
	}

	@Override
	public long getUnreadMessagesCount() {
		return repository.countAllUnreadByUserId(getUserId());
	}

	@Override
	public void toggleMarkAsRead(MarkAsReadRequestDto dto) {
		repository.markAsRead(getUserId(), dto.getIds(), dto.isOpened());
	}

	@Override
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	@Override
	public void deleteAllByIds(IdListDto dto) {
		repository.deleteAllByUserIdAndIds(getUserId(), dto.getIds());
	}

	@Async
	@Override
	public void batchDeleteByUserIds(Set<Long> userIds) {
		repository.deleteAllByUserId(userIds);
		log.info("All messages have been removed for the requested users");
	}
}