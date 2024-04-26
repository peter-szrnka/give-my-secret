package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.GmsException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemPropertyConverterTest extends AbstractUnitTest {

	private final Clock clock = mock(Clock.class);
	private final SystemPropertyConverter converter = new SystemPropertyConverter(clock);

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
		assertEquals(SystemProperty.values().length, response.getTotalElements());
		assertEquals("Budapest", response.getResultList().stream()
			.filter(property -> property.getKey().equals("ORGANIZATION_CITY"))
			.map(SystemPropertyDto::getValue).findFirst().get());
		assertTrue(response.getResultList().stream().noneMatch(item -> item.getType() == null || item.getKey() == null || item.getValue() == null));
		assertEquals(SystemProperty.values().length-1, response.getResultList().stream().filter(SystemPropertyDto::isFactoryValue).count());
		assertEquals(1L, response.getResultList().stream().filter((item) -> item.getLastModified() != null).count());
	}
	
	@Test
	void shouldConvertToNewEntity() {
		// act
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SystemPropertyEntity response = converter.toEntity(null, SystemPropertyDto.builder().key(SystemProperty.JOB_OLD_EVENT_LIMIT.name()).value("1;d").build());
		
		// assert
		assertNotNull(response);
		assertNull(response.getId());
		assertEquals(SystemProperty.JOB_OLD_EVENT_LIMIT, response.getKey());
		assertEquals("1;d", response.getValue());
	}

	@Test
	void shouldConvertExistingEntity() {
		// act
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		SystemPropertyEntity entity = new SystemPropertyEntity();
		entity.setId(2L);
		entity.setLastModified(ZonedDateTime.now(clock));
		SystemPropertyEntity response = converter.toEntity(entity, SystemPropertyDto.builder().key(SystemProperty.JOB_OLD_EVENT_LIMIT.name()).value("1;d").build());
		
		// assert
		assertNotNull(response);
		assertEquals("SystemPropertyEntity(id=2, key=JOB_OLD_EVENT_LIMIT, value=1;d, lastModified=2023-06-29T00:00Z)", response.toString());
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