package io.github.gms.functions.event;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.UserEvent;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventServiceImplTest extends AbstractUnitTest {

	@Mock
	private Clock clock;
	@Mock
	private EventRepository repository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private EventConverter converter;
	@Mock
	private UnprocessedEventStorage unprocessedEventStorage;
	@InjectMocks
	private EventServiceImpl service;
	
	@Test
	void saveUserEvent_whenUserEventOccurred_thenReturnOk() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");

		// act
		service.saveUserEvent(UserEvent.builder()
						.userId(1L)
						.operation(EventOperation.GET_BY_ID)
						.target(EventTarget.API_KEY)
				.build());
		
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
	void delete_whenInputProvided_thenReturnOk() {
		// act
		service.delete(1L);

		// assert
		verify(repository).deleteById(1L);
	}

	@Test
	void list_whenInputProvided_thenReturnOk() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		
		EventEntity secondEventEntity = TestUtils.createEventEntity();
		secondEventEntity.setUserId(0L);

		EventEntity event1 = TestUtils.createEventEntity();

		EventEntity event3 = TestUtils.createEventEntity();
		event3.setUserId(-1L);

		EventDto mockEvent1 = new EventDto();
		mockEvent1.setUsername("user1");
		EventDto mockEvent2 = new EventDto();
		mockEvent2.setUsername("user2");
		EventDto mockEvent3 = new EventDto();
		mockEvent3.setUsername("user3");
		
		Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(event1, secondEventEntity, event3));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		when(converter.toDto(eq(event1), anyString())).thenReturn(mockEvent1);
		when(converter.toDto(eq(secondEventEntity), anyString())).thenReturn(mockEvent2);
		when(converter.toDto(eq(event3), anyString())).thenReturn(mockEvent3);
		when(userRepository.getUsernameById(anyLong())).thenReturn("user1");
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// act
		EventListDto response = service.list(pageable);

		// assert
		assertNotNull(response);
		assertEquals(3, response.getResultList().size());
		assertEquals(3L, response.getTotalElements());
		verify(repository).findAll(any(Pageable.class));
		verify(converter, times(3)).toDto(any(EventEntity.class), anyString());
		verify(userRepository).getUsernameById(anyLong());

		assertEquals("user1", response.getResultList().get(0).getUsername());
		assertEquals("user2", response.getResultList().get(1).getUsername());
	}
	
	@Test
	void listByUser_whenInputProvided_thenReturnOk() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createEventEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any(), eq("user1"))).thenReturn(EventListDto.builder()
				.resultList(Lists.newArrayList(new EventDto()))
				.totalElements(1).build());
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		when(userRepository.getUsernameById(anyLong())).thenReturn("user1");

		// act
		EventListDto response = service.listByUser(1L, pageable);

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
		verify(converter).toDtoList(any(), eq("user1"));
		verify(userRepository).getUsernameById(anyLong());
	}
}
