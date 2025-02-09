package io.github.gms.functions.message;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageServiceImplTest extends AbstractLoggingUnitTest {

	private Clock clock;
	private MessageRepository repository;
	private MessageConverter converter;
	private MessageServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		// Init
		clock = mock(Clock.class);
		repository = mock(MessageRepository.class);
		converter = mock(MessageConverter.class);
		service = new MessageServiceImpl(clock, repository, converter);
		addAppender(MessageServiceImpl.class);
	}

	@Test
	void save_whenInputProvided_thenReturnOk() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MessageDto dto = MessageDto.builder()
				.message("test message")
				.userId(2L)
				.actionPath("/test")
				.build();
		when(repository.save(any(MessageEntity.class))).thenReturn(TestUtils.createMessageEntity());
		
		// act
		SaveEntityResponseDto response = service.save(dto);

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		ArgumentCaptor<MessageEntity> messageEntityCaptor = ArgumentCaptor.forClass(MessageEntity.class);
		verify(repository).save(any());
		verify(repository).save(messageEntityCaptor.capture());
		
		MessageEntity capturedEntity = messageEntityCaptor.getValue();
		assertNull(capturedEntity.getId());
		assertEquals(2L, capturedEntity.getUserId());
		assertEquals("test message", capturedEntity.getMessage());
		assertFalse(capturedEntity.isOpened());
		assertNotNull(capturedEntity.getCreationDate());
		assertEquals("/test", capturedEntity.getActionPath());
	}
	
	@Test
	void list_whenInputProvided_thenReturnOk() {
		// arrange

		Page<MessageEntity> mockList = new PageImpl<>(Lists.newArrayList(new MessageEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(MessageListDto.builder()
				.resultList(Lists.newArrayList(new MessageDto()))
				.totalElements(1).build());
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// act
		MessageListDto response = service.list(pageable);

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
		verify(converter).toDtoList(any());
	}
	
	@Test
	void getUnreadMessagesCount_whenInputProvided_thenReturnOk() {
		// arrange
		when(repository.countAllUnreadByUserId(1L)).thenReturn(2L);

		// act
		long count = service.getUnreadMessagesCount();
		
		// assert
		assertEquals(2L, count);
		verify(repository).countAllUnreadByUserId(1L);
	}
	
	@Test
	void toggleMarkAsRead_whenInputProvided_thenReturnOk() {
		// arrange
		MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().ids(Sets.newHashSet(2L)).opened(true).build();

		// act
		service.toggleMarkAsRead(dto);

		// assert
		verify(repository).markAsRead(1L, dto.getIds(), true);
	}

	@Test
	void batchDeleteByUserIds_whenInputProvided_thenReturnOk() {
		// arrange
		Set<Long> userIds = Set.of(1L, 2L);

		// act
		service.batchDeleteByUserIds(userIds);

		// assert
		verify(repository).deleteAllByUserId(userIds);
		assertLogContains(logAppender, "All messages have been removed for the requested users");
	}

	@Test
	void deleteAllByIds_whenInputProvided_thenReturnOk() {
		// arrange
		IdListDto input = new IdListDto(Set.of(1L, 2L, 3L));
		// act
		service.deleteAllByIds(input);

		// assert
		verify(repository).deleteAllByUserIdAndIds(anyLong(), eq(input.getIds()));
	}

	@Test
	void deleteById_whenInputProvided_thenReturnOk() {
		// arrange
		Long id = 1L;

		// act
		service.deleteById(id);

		// assert
		verify(repository).deleteById(id);
	}
}
