package io.github.gms.functions.message;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.AbstractClientControllerIntegrationTest;
import io.github.gms.common.TestedClass;
import io.github.gms.common.TestedMethod;
import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
@TestedClass(MessageController.class)
class MessageIntegrationTest extends AbstractClientControllerIntegrationTest {
	
	@Autowired
	private MessageRepository repository;

	protected MessageIntegrationTest() {
		super("/secure/message");
	}
	
	@Test
	@TestedMethod("markAsRead")
	void testMarkAsRead() {
		// arrange
		MessageEntity newEntity = repository.save(TestUtils.createNewMessageEntity());
		
		// act
		MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().ids(Sets.newHashSet(newEntity.getId())).build();
		HttpEntity<MarkAsReadRequestDto> requestEntity = new HttpEntity<>(dto, TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpPut(requestEntity);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@Test
	@TestedMethod("list")
	void testList() {
		repository.deleteAll();

		// arrange
		repository.save(TestUtils.createNewMessageEntity());
		
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>( TestUtils.getHttpHeaders(jwt));
		ResponseEntity<MessageListDto> response = executeHttpGet("/list?page=0&size=10&direction=ASC&property=id", requestEntity, MessageListDto.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		
		MessageListDto responseList = response.getBody();
		assertEquals(1, responseList.getResultList().size());
	}
	
	@Test
	@TestedMethod("unreadMessagesCount")
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

	@Test
	@TestedMethod("deleteAllByIds")
	void testDeleteAllByIds() {
		// act
		HttpEntity<IdListDto> requestEntity =
				new HttpEntity<>(new IdListDto(Set.of(1L, 2L)), TestUtils.getHttpHeaders(jwt));
		ResponseEntity<Void> response = executeHttpDelete("/delete_all_by_ids", requestEntity, Void.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	@TestedMethod("deleteById")
	void testDeleteById() {
		// act
		HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));
		ResponseEntity<String> response = executeHttpDelete("/1", requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNull(response.getBody());
	}
}
