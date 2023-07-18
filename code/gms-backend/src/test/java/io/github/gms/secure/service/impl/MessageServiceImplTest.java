package io.github.gms.secure.service.impl;

import com.google.common.collect.Sets;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.converter.MessageConverter;
import io.github.gms.secure.dto.MarkAsReadRequestDto;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.secure.repository.MessageRepository;
import io.github.gms.util.TestUtils;
import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageServiceImplTest extends AbstractUnitTest {

	private Clock clock;
	private MessageRepository repository;
	private MessageConverter converter;
	private MessageServiceImpl service;

	@BeforeEach
	public void setup() {
		clock = mock(Clock.class);
		repository = mock(MessageRepository.class);
		converter = mock(MessageConverter.class);
		service = new MessageServiceImpl(clock, repository, converter);
	}
	@Test
	void shouldSave() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MessageDto dto = MessageDto.builder()
				.message("test message")
				.build();
		when(repository.save(any(MessageEntity.class))).thenReturn(TestUtils.createMessageEntity());
		
		// act
		SaveEntityResponseDto response = service.save(dto);

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		ArgumentCaptor<MessageEntity> messageEntityCaptor = ArgumentCaptor.forClass(MessageEntity.class);
		verify(repository).save(messageEntityCaptor.capture());
		
		MessageEntity capturedEntity = messageEntityCaptor.getValue();
		assertEquals("MessageEntity(id=null, userId=null, message=test message, opened=false, creationDate=2023-06-29T00:00Z)", capturedEntity.toString());
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		Page<MessageEntity> mockList = new PageImpl<>(Lists.newArrayList(new MessageEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(MessageListDto.builder()
				.resultList(Lists.newArrayList(new MessageDto()))
				.totalElements(1).build());

		// act
		MessageListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
		verify(converter).toDtoList(any());
		
		MDC.clear();
	}
	
	@Test
	void shouldDelete() {
		// act
		service.delete(1L);

		// assert
		verify(repository).deleteById(1L);
	}
	
	@Test
	void shouldReturnUnreadMessagesCount() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		when(repository.countAllUnreadByUserId(1L)).thenReturn(2L);

		// act
		long count = service.getUnreadMessagesCount();
		
		// assert
		assertEquals(2l, count);
		verify(repository).countAllUnreadByUserId(1L);
		
		MDC.clear();
	}
	
	@Test
	void shouldMarkAsRead() {
		// arrange
		MarkAsReadRequestDto dto = MarkAsReadRequestDto.builder().ids(Sets.newHashSet(2L)).build();
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);

		// act
		service.markAsRead(dto);

		// assert
		verify(repository).markAsRead(1L, dto.getIds());
	}
}
