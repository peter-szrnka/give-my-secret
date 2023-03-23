package io.github.gms.controller;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.MarkAsReadRequestDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.secure.repository.MessageRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
class MessageIntegrationTest extends AbstractClientControllerIntegrationTest {
	
	@Autowired
	private MessageRepository repository;

	protected MessageIntegrationTest() {
		super("/secure/message");
	}
	
	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsUser();
		jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest());
	}
	
	@Test
	void testMarkAsRead() {
		// arrange
		MessageEntity newEntity = repository.save(TestUtils.createNewMessageEntity());
		
		// act
		MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().ids(Sets.newHashSet(newEntity.getId())).build();
		HttpEntity<MarkAsReadRequestDto> requestEntity = new HttpEntity<>(dto, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPut("/mark_as_read", requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	void testList() {
		repository.deleteAll();

		// arrange
		repository.save(TestUtils.createNewMessageEntity());
		
		// act
		PagingDto request = PagingDto.builder().page(0).size(50).direction("ASC").property("id").build();

		HttpEntity<PagingDto> requestEntity = new HttpEntity<>(request, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<MessageListDto> response = executeHttpPost("/list", requestEntity, MessageListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		MessageListDto responseList = response.getBody();
		assertEquals(1, responseList.getResultList().size());
	}
	
	@Test
	void testGetUnreadMessagesCount() {
		repository.deleteAll();
		
		// arrange
		repository.save(TestUtils.createNewMessageEntity());

		// act
		MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().ids(Sets.newHashSet(1L)).build();
		HttpEntity<MarkAsReadRequestDto> requestEntity = new HttpEntity<>(dto, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<LongValueDto> response = executeHttpGet("/unread", requestEntity, LongValueDto.class);
		
		// assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		LongValueDto responseBody = response.getBody();
		assertEquals(1, responseBody.getValue());
	}
}
