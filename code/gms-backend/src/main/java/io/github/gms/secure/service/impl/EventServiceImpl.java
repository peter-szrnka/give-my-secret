package io.github.gms.secure.service.impl;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.gms.common.enums.MdcParameter;
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
	
	@Autowired
	private Clock clock;
	
	@Autowired
	private EventRepository repository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EventConverter converter;

	@Override
	public void saveUserEvent(UserEvent event) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		EventEntity entity = new EventEntity();
		entity.setEventDate(ZonedDateTime.now(clock));
		entity.setUserId(userId);
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
		return new EventListDto(repository.findAll(ConverterUtils.createPageable(dto)).toList()
				.stream()
				.map(entity -> converter.toDto(entity, getUsername(entity.getUserId())))
				.collect(Collectors.toList()));
	}

	@Override
	public EventListDto listByUser(Long userId, PagingDto dto) {
		return converter.toDtoList(repository.findAllByUserId(userId, ConverterUtils.createPageable(dto)).toList(), getUsername(userId));
	}
	
	private String getUsername(Long userId) {
		return userId.equals(0L) ? "setup" : userRepository.getUsernameById(userId);
	}
}
