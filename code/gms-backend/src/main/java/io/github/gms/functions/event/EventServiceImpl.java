package io.github.gms.functions.event;

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

import static io.github.gms.common.util.Constants.GENERIC_USER;
import static io.github.gms.common.util.Constants.JOB_USER;
import static java.util.Optional.ofNullable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final Clock clock;
	private final EventRepository repository;
	private final UserRepository userRepository;
	private final EventConverter converter;
	private final UnprocessedEventStorage unprocessedEventStorage;

	@Override
	public void saveUserEvent(UserEvent event) {
		EventEntity entity = new EventEntity();
		entity.setEntityId(event.getEntityId());
		entity.setEventDate(ofNullable(event.getEventDate()).orElse(ZonedDateTime.now(clock)));
		entity.setUserId(event.getUserId());
		entity.setOperation(event.getOperation());
		entity.setSource(event.getEventSource());
		entity.setTarget(event.getTarget());
		repository.save(entity);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public EventListDto list(Pageable pageable) {
		Page<EventEntity> results = repository.findAll(pageable);
		return EventListDto.builder().resultList(results.toList().stream()
						.map(entity -> converter.toDto(entity, getUsername(entity.getUserId())))
						.toList()).totalElements(results.getTotalElements()).build();
	}

	@Override
	public EventListDto listByUser(Long userId, Pageable pageable) {
		return converter.toDtoList(repository.findAllByUserId(userId, pageable), getUsername(userId));
	}

	@Override
	public int getUnprocessedEventsCount() {
		return unprocessedEventStorage.getAll(false).size();
	}

	@Async
	@Override
	public void batchDeleteByUserIds(Set<Long> userIds) {
		repository.deleteAllByUserId(userIds);
		log.info("All events have been removed for the requested users");
	}
	
	private String getUsername(Long userId) {
		if (GENERIC_USER.equals(userId)) {
			return "technical user";
		}

		return userId.equals(JOB_USER) ? "job" : userRepository.getUsernameById(userId);
	}
}
