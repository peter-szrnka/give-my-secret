package io.github.gms.functions.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.gms.functions.event.EventConverterImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.functions.event.EventConverter;
import io.github.gms.functions.event.EventDto;
import io.github.gms.functions.event.EventListDto;
import io.github.gms.functions.event.EventEntity;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class EventConverterImplTest extends AbstractUnitTest {

	private final EventConverter converter = new EventConverterImpl();

	@Test
	void checkToList() {
		// arrange
		Page<EventEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createEventEntity()));

		// act
		EventListDto resultList = converter.toDtoList(entityList, "username");

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
		assertEquals(1L, resultList.getTotalElements());

		EventDto dto = resultList.getResultList().get(0);
		assertEquals(1L, dto.getId());
		assertNotNull(dto.getEventDate());
		assertEquals(EventOperation.GET_BY_ID, dto.getOperation());
		assertEquals(EventTarget.KEYSTORE, dto.getTarget());
		assertEquals(DemoData.USER_1_ID, dto.getUserId());
		assertEquals("username", dto.getUsername());
	}
}
