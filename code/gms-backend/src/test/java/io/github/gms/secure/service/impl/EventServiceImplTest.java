package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;

import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventDto;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.secure.model.UserEvent;
import io.github.gms.secure.repository.EventRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link EventServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EventServiceImplTest extends AbstractUnitTest {

	@Mock
	private Clock clock;

	@InjectMocks
	private EventServiceImpl service;
	
	@Mock
	private UserRepository userRepository;

	@Mock
	private EventRepository repository;

	@Mock
	private EventConverter converter;
	
	@Test
	void shouldSaveUserEvent() {
		// arrange
		setupClock(clock);
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");

		// act
		service.saveUserEvent(new UserEvent(EventOperation.GET_BY_ID, EventTarget.API_KEY));
		
		// assert
		ArgumentCaptor<EventEntity> eventCaptor = ArgumentCaptor.forClass(EventEntity.class);
		verify(repository).save(eventCaptor.capture());
		
		EventEntity capturedEvent = eventCaptor.getValue();
		assertEquals(1L, capturedEvent.getUserId());
		assertEquals(EventOperation.GET_BY_ID, capturedEvent.getOperation());
		assertEquals(EventTarget.API_KEY, capturedEvent.getTarget());
	}

	@Test
	void shouldDelete() {
		// act
		service.delete(1L);

		// assert
		verify(repository).deleteById(1L);
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		
		EventEntity secondEventEntity = TestUtils.createEventEntity();
		secondEventEntity.setUserId(0L);
		
		Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createEventEntity(), secondEventEntity));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		when(converter.toDto(any(EventEntity.class), anyString())).thenReturn(new EventDto());
		when(userRepository.getUsernameById(anyLong())).thenReturn("user1");

		// act
		EventListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(2, response.getResultList().size());
		verify(repository).findAll(any(Pageable.class));
		verify(converter, times(2)).toDto(any(EventEntity.class), anyString());
		verify(userRepository).getUsernameById(anyLong());
	}
	
	@Test
	void shouldReturnListByUser() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createEventEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any(), anyString())).thenReturn(new EventListDto(Lists.newArrayList(new EventDto())));
		when(userRepository.getUsernameById(anyLong())).thenReturn("user1");

		// act
		EventListDto response = service.listByUser(1L, new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
		verify(converter).toDtoList(any(), anyString());
		verify(userRepository).getUsernameById(anyLong());
	}
}
