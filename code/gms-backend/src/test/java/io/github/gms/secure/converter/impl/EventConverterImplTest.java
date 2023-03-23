package io.github.gms.secure.converter.impl;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.secure.converter.EventConverter;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventConverterImplTest extends AbstractUnitTest {

	private final EventConverter converter = new EventConverterImpl();

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
