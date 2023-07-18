package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.secure.dto.MessageDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageConverterImplTest extends AbstractUnitTest {

	private final MessageConverterImpl converter = new MessageConverterImpl();

	@Test
	void checkToList() {
		// arrange
		Clock clock = mock(Clock.class);
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		MessageEntity entity = TestUtils.createMessageEntity();
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setOpened(true);
		Page<MessageEntity> entityList = new PageImpl<>(Lists.newArrayList(entity));

		// act
		MessageListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
		assertEquals(1L, resultList.getTotalElements());

		MessageDto dto = resultList.getResultList().get(0);
		assertEquals("MessageDto(id=1, userId=1, opened=true, message=test message, creationDate=2023-06-29T00:00Z)", dto.toString());
	}
}
