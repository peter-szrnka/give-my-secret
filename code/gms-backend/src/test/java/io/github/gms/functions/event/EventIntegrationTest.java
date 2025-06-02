package io.github.gms.functions.event;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.abstraction.GmsControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.IntegerValueDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;

import static io.github.gms.util.TestConstants.LIST;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(EventController.class)
class EventIntegrationTest extends AbstractIntegrationTest implements GmsControllerIntegrationTest {
	
	private final String path = "/secure/event";
	
	@Autowired
	private EventRepository eventRepository;
	
	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		gmsUser = TestUtils.createGmsAdminUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
	}

	@Test
	@TestedMethod(LIST)
	void list_whenInputIsValid_thenReturnOk() {
		eventRepository.deleteAll();
		
		// arrange
		EventEntity eventEntity = new EventEntity();
		eventEntity.setId(1L);
		eventEntity.setUserId(2L);
		eventEntity.setOperation(EventOperation.GET_BY_ID);
		eventEntity.setTarget(EventTarget.API_KEY);
		eventEntity.setEventDate(ZonedDateTime.now().minusDays(1L));
		eventRepository.save(eventEntity);

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<EventListDto> response = executeHttpGet(path + "/list?page=0&size=10&direction=ASC&property=id", requestEntity, EventListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		EventListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
		assertEquals(EventOperation.GET_BY_ID, responseList.getResultList().getFirst().getOperation());
		assertEquals(EventTarget.API_KEY, responseList.getResultList().getFirst().getTarget());
	}
	
	@Test
	@TestedMethod("listByUserId")
	void listByUserId_whenInputIsValid_thenReturnOk() {
		eventRepository.deleteAll();
		
		// arrange
		EventEntity eventEntity = new EventEntity();
		eventEntity.setId(1L);
		eventEntity.setUserId(2L);
		eventEntity.setOperation(EventOperation.GET_BY_ID);
		eventEntity.setTarget(EventTarget.API_KEY);
		eventEntity.setEventDate(ZonedDateTime.now().minusDays(1L));
		eventRepository.save(eventEntity);

		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<EventListDto> response = executeHttpGet(path + "/list/2?page=0&size=10&direction=ASC&property=id", requestEntity, EventListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		EventListDto responseList = response.getBody();
		assertFalse(responseList.getResultList().isEmpty());
		assertEquals(EventOperation.GET_BY_ID, responseList.getResultList().getFirst().getOperation());
		assertEquals(EventTarget.API_KEY, responseList.getResultList().getFirst().getTarget());
	}

	@Test
	@TestedMethod("getUnprocessedEventsCount")
	void getUnprocessedEventsCount_whenCalled_thenReturnOk() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<IntegerValueDto> response = executeHttpGet(path + "/unprocessed", requestEntity, IntegerValueDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}
}
