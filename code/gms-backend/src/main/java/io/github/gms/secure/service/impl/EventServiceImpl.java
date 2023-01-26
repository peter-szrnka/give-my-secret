package io.github.gms.secure.service.impl;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.secure.model.UserEvent;
import io.github.gms.secure.repository.EventRepository;
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
	private EventConverter converter;

	@Override
	public void saveUserEvent(UserEvent event) {
		String username = MDC.get(MdcParameter.USER_NAME.getDisplayName());
		
		EventEntity entity = new EventEntity();
		entity.setEventDate(ZonedDateTime.now(clock));
		entity.setUserId(username);
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
		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		Pageable pagingRequest = PageRequest.of(dto.getPage(), dto.getSize(), sort);

		Page<EventEntity> resultList = repository.findAll(pagingRequest);
		return converter.toDtoList(resultList);
	}
}
