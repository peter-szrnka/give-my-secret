package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
 * @author Peter Szrnka
 * @since 1.0
 */
class EventServiceImplTest extends AbstractUnitTest {

	private Clock clock;
	private EventRepository repository;
	private UserRepository userRepository;
	private EventConverter converter;
	private EventServiceImpl service;

	@BeforeEach
	public void setup() {
		clock = mock(Clock.class);
		repository = mock(EventRepository.class);
		userRepository = mock(UserRepository.class);
		converter = mock(EventConverter.class);
		service = new EventServiceImpl(clock, repository, userRepository, converter);
	}
	
	@Test
	void shouldSaveUserEvent() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
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
		assertEquals("2023-06-29T00:00Z", capturedEvent.getEventDate().toString());
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

		EventEntity event1 = TestUtils.createEventEntity();

		EventDto mockEvent1 = new EventDto();
		mockEvent1.setUsername("user1");
		EventDto mockEvent2 = new EventDto();
		mockEvent2.setUsername("user2");
		
		Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(event1, secondEventEntity));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		when(converter.toDto(eq(event1), anyString())).thenReturn(mockEvent1);
		when(converter.toDto(eq(secondEventEntity), anyString())).thenReturn(mockEvent2);
		when(userRepository.getUsernameById(anyLong())).thenReturn("user1");

		// act
		EventListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(2, response.getResultList().size());
		assertEquals(2L, response.getTotalElements());
		verify(repository).findAll(any(Pageable.class));
		verify(converter, times(2)).toDto(any(EventEntity.class), anyString());
		verify(userRepository).getUsernameById(anyLong());

		assertEquals("user1", response.getResultList().get(0).getUsername());
		assertEquals("user2", response.getResultList().get(1).getUsername());
	}
	
	@Test
	void shouldReturnListByUser() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createEventEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any(), anyString())).thenReturn(EventListDto.builder()
				.resultList(Lists.newArrayList(new EventDto()))
				.totalElements(1).build());

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
