package io.github.gms.functions.event;

import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.model.UserEvent;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.MdcUtils.getUserId;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final Clock clock;
	private final EventRepository repository;
	private final UserRepository userRepository;
	private final EventConverter converter;

	@Override
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

	@Override
	public EventListDto list(Pageable pageable) {
		Page<EventEntity> results = repository.findAll(pageable);
		return EventListDto.builder().resultList(results.toList().stream()
						.map(entity -> converter.toDto(entity, getUsername(entity.getUserId())))
						.toList()).totalElements(results.getTotalElements()).build();
	}

	@Override
	public EventListDto listByUser(Long userId, PagingDto dto) {
		return converter.toDtoList(repository.findAllByUserId(userId, ConverterUtils.createPageable(dto)), getUsername(userId));
	}
	
	private String getUsername(Long userId) {
		return userId.equals(0L) ? "setup" : userRepository.getUsernameById(userId);
	}
}
