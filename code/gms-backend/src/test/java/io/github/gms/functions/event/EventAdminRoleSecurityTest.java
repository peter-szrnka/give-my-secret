package io.github.gms.functions.event;

import io.github.gms.abstraction.AbstractSecurityTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
@TestedClass(EventController.class)
class EventAdminRoleSecurityTest extends AbstractSecurityTest {

	public EventAdminRoleSecurityTest() {
		super("/event");
	}

	@Test
	@TestedMethod("list")
	void list_whenAuthenticationFails_thenReturnHttp403() {
		assertListFailWith403(EventListDto.class);
	}

	@Test
	@TestedMethod("listByUserId")
	void listByUserId_whenAuthenticationFails_thenReturnHttp403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(null));

		// act
		ResponseEntity<EventListDto> response = executeHttpGet(urlPrefix + "/list/1", requestEntity, EventListDto.class);

		// assert
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	@Test
	@TestedMethod("getUnprocessedEventsCount")
	void getUnprocessedEventsCount_whenCalled_thenReturn403() {
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(null));
		ResponseEntity<Integer> response = executeHttpGet(urlPrefix + "/unprocessed", requestEntity, Integer.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}
}