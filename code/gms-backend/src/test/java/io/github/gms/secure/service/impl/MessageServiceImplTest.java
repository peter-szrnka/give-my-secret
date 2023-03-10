package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;

import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageServiceImplTest extends AbstractUnitTest {

	@Mock
	private Clock clock;
	@Mock
	private MessageRepository repository;
	@Mock
	private MessageConverter converter;
	@InjectMocks
	private MessageServiceImpl service;
	
	@Test
	void shouldSave() {
		// arrange
		setupClock(clock);
		MessageDto dto = MessageDto.builder()
				.message("test message")
				.build();
		when(repository.save(any(MessageEntity.class))).thenReturn(TestUtils.createMessageEntity());
		
		// act
		SaveEntityResponseDto response = service.save(dto);

		// assert
		assertNotNull(response);
		ArgumentCaptor<MessageEntity> messageEntityCaptor = ArgumentCaptor.forClass(MessageEntity.class);
		verify(repository).save(messageEntityCaptor.capture());
		
		MessageEntity capturedEntity = messageEntityCaptor.getValue();
		assertEquals("test message", capturedEntity.getMessage());
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		Page<MessageEntity> mockList = new PageImpl<>(Lists.newArrayList(new MessageEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(new MessageListDto(Lists.newArrayList(new MessageDto())));

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
