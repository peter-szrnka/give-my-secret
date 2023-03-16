package io.github.gms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.secure.repository.EventRepository;
import io.github.gms.util.TestConstants;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class EventIntegrationTest extends AbstractIntegrationTest {
	
	private final String path = "/secure/event";
	
	@Autowired
	private EventRepository eventRepository;
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtAdminRequest(gmsUser));
	}

	@Test
	void testList() {
		eventRepository.deleteAll();
		
		// arrange
		EventEntity eventEntity = new EventEntity();
		eventEntity.setId(1L);
		eventEntity.setUserId(2L);
		eventEntity.setOperation(EventOperation.GET_BY_ID);
		eventEntity.setTarget(EventTarget.API_KEY);
		eventEntity.setEventDate(ZonedDateTime.now().minusDays(1l));
		eventRepository.save(eventEntity);

		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<EventListDto> response = executeHttpPost(path + "/list", requestEntity, EventListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		EventListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
		assertEquals(EventOperation.GET_BY_ID, responseList.getResultList().get(0).getOperation());
		assertEquals(EventTarget.API_KEY, responseList.getResultList().get(0).getTarget());
	}
	
	@Test
	void testListByUserId() {
		eventRepository.deleteAll();
		
		// arrange
		EventEntity eventEntity = new EventEntity();
		eventEntity.setId(1L);
		eventEntity.setUserId(2L);
		eventEntity.setOperation(EventOperation.GET_BY_ID);
		eventEntity.setTarget(EventTarget.API_KEY);
		eventEntity.setEventDate(ZonedDateTime.now().minusDays(1l));
		eventRepository.save(eventEntity);

		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<EventListDto> response = executeHttpPost(path + "/list/2", requestEntity, EventListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		EventListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
		assertEquals(EventOperation.GET_BY_ID, responseList.getResultList().get(0).getOperation());
		assertEquals(EventTarget.API_KEY, responseList.getResultList().get(0).getTarget());
	}
}
