package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.secure.converter.MessageConverter;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link MessageConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class MessageConverterImplTest extends AbstractUnitTest {

	private MessageConverter converter = new MessageConverterImpl();

	@Test
	void checkToList() {
		// arrange
		Page<MessageEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createMessageEntity()));

		// act
		MessageListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
	}
}
