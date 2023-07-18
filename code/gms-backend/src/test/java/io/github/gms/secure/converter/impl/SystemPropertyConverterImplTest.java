package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemPropertyConverterImplTest extends AbstractUnitTest {

	private Clock clock = mock(Clock.class);
	private final SystemPropertyConverterImpl converter = new SystemPropertyConverterImpl(clock);

	@Test
	void shouldConvertToDtoList() {
		// arrange
		SystemPropertyEntity entity = new SystemPropertyEntity();
		entity.setKey(SystemProperty.ORGANIZATION_CITY);
		entity.setValue("Budapest");
		entity.setLastModified(ZonedDateTime.now());

		SystemPropertyEntity entity2 = new SystemPropertyEntity();
		entity2.setKey(null);
		entity2.setValue("Test value");
		entity2.setLastModified(ZonedDateTime.now());

		// act
		SystemPropertyListDto response = converter.toDtoList(List.of(entity, entity2));
		
		// assert
		assertNotNull(response);
		assertFalse(response.getResultList().isEmpty());
		assertEquals(7L, response.getTotalElements());
		assertEquals("Budapest", response.getResultList().stream()
			.filter(property -> property.getKey().equals("ORGANIZATION_CITY"))
			.map(item -> item.getValue()).findFirst().get());
		assertTrue(response.getResultList().stream().noneMatch(item -> item.getType() == null || item.getKey() == null || item.getValue() == null));
		assertEquals(6L, response.getResultList().stream().filter((item) -> item.isFactoryValue()).count());
		assertEquals(1L, response.getResultList().stream().filter((item) -> item.getLastModified() != null).count());
	}
	
	@Test
	void shouldConvertToNewEntity() {
		// act
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SystemPropertyEntity response = converter.toEntity(null, SystemPropertyDto.builder().key(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS.name()).value("1").build());
		
		// assert
		assertNotNull(response);
		assertNull(response.getId());
		assertEquals(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS, response.getKey());
		assertEquals("1", response.getValue());
	}

	@Test
	void shouldConvertExistingEntity() {
		// act
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		SystemPropertyEntity entity = new SystemPropertyEntity();
		entity.setId(2L);
		entity.setLastModified(ZonedDateTime.now(clock));
		SystemPropertyEntity response = converter.toEntity(entity, SystemPropertyDto.builder().key(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS.name()).value("1").build());
		
		// assert
		assertNotNull(response);
		assertEquals("SystemPropertyEntity(id=2, key=OLD_EVENT_TIME_LIMIT_DAYS, value=1, lastModified=2023-06-29T00:00Z)", response.toString());
	}
	
	@Test
	void shouldNotConvertToExistingEntity() {
		// arrange
		SystemPropertyEntity entity = new SystemPropertyEntity();
		SystemPropertyDto dto = SystemPropertyDto.builder().key("invalid").value("1").build();
		
		// act
		GmsException exception = assertThrows(GmsException.class, () -> converter.toEntity(entity, dto));
		
		// assert
		assertEquals("Unknown system property!", exception.getMessage());
	}
}