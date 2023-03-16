package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link EventConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class EventConverterImplTest extends AbstractUnitTest {

	private EventConverter converter = new EventConverterImpl();

	@Test
	void checkToList() {
		// arrange
		List<EventEntity> entityList = Lists.newArrayList(TestUtils.createEventEntity());

		// act
		EventListDto resultList = converter.toDtoList(entityList, "username");

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
	}
}
