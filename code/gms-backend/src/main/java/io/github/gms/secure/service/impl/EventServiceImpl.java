package io.github.gms.secure.service.impl;

import static io.github.gms.common.util.MdcUtils.getUserId;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.github.gms.common.util.ConverterUtils;
import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.secure.model.UserEvent;
import io.github.gms.secure.repository.EventRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.EventService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class EventServiceImpl implements EventService {

	private final Clock clock;
	private final EventRepository repository;
	private final UserRepository userRepository;
	private final EventConverter converter;

	public EventServiceImpl(Clock clock, EventRepository repository,
							UserRepository userRepository, EventConverter converter) {
		this.clock = clock;
		this.repository = repository;
		this.userRepository = userRepository;
		this.converter = converter;
	}

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
	public EventListDto list(PagingDto dto) {
		Page<EventEntity> results = repository.findAll(ConverterUtils.createPageable(dto));
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
