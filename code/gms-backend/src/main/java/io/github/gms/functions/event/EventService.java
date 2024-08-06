package io.github.gms.functions.event;

import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.common.abstraction.GmsService;
import io.github.gms.common.model.UserEvent;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Set;

import static io.github.gms.common.util.MdcUtils.getUserId;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventService implements GmsService, BatchDeletionService {

	private final Clock clock;
	private final EventRepository repository;
	private final UserRepository userRepository;
	private final EventConverter converter;

	public void saveUserEvent(UserEvent event) {
		EventEntity entity = new EventEntity();
		entity.setEventDate(ZonedDateTime.now(clock));
		entity.setUserId(getUserId());
		entity.setOperation(event.getOperation());
		entity.setTarget(event.getTarget());
		repository.save(entity);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	public EventListDto list(Pageable pageable) {
		Page<EventEntity> results = repository.findAll(pageable);
		return EventListDto.builder().resultList(results.toList().stream()
						.map(entity -> converter.toDto(entity, getUsername(entity.getUserId())))
						.toList()).totalElements(results.getTotalElements()).build();
	}

	public EventListDto listByUser(Long userId, Pageable pageable) {
		return converter.toDtoList(repository.findAllByUserId(userId, pageable), getUsername(userId));
	}

	@Async
	@Override
	public void batchDeleteByUserIds(Set<Long> userIds) {
		repository.deleteAllByUserId(userIds);
		log.info("All events have been removed for the requested users");
	}
	
	private String getUsername(Long userId) {
		return userId.equals(0L) ? "setup" : userRepository.getUsernameById(userId);
	}
}
