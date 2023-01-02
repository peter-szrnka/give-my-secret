package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.entity.EventEntity;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventDto;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.model.UserEvent;
import io.github.gms.secure.repository.EventRepository;

/**
 * Unit test of {@link EventServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EventServiceImplTest extends AbstractUnitTest {

	@InjectMocks
	private EventServiceImpl service;

	@Mock
	private EventRepository repository;

	@Mock
	private EventConverter converter;
	
	@Test
	void shouldSaveUserEvent() {
		// arrange
		setupClock();
		MDC.put(MdcParameter.USER_NAME.getDisplayName(), "user1");

		// act
		service.saveUserEvent(new UserEvent(EventOperation.GET_BY_ID, EventTarget.API_KEY));
		
		// assert
		ArgumentCaptor<EventEntity> eventCaptor = ArgumentCaptor.forClass(EventEntity.class);
		verify(repository).save(eventCaptor.capture());
		
		EventEntity capturedEvent = eventCaptor.getValue();
		assertEquals("user1", capturedEvent.getUserId());
		assertEquals(EventOperation.GET_BY_ID, capturedEvent.getOperation());
		assertEquals(EventTarget.API_KEY, capturedEvent.getTarget());
	}

	@Test
	void shouldReturnList() {
		// arrange
		//Page<EventEntity> mockList = new PageImpl<>(Lists.newArrayList(new EventEntity()));
		when(converter.toDtoList(any())).thenReturn(new EventListDto(Lists.newArrayList(new EventDto())));

		// act
		EventListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(converter).toDtoList(any());
	}
	
	@Test
	void shouldDelete() {
		// act
		service.delete(1L);

		// assert
		verify(repository).deleteById(1L);
	}
}
