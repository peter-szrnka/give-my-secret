package io.github.gms.secure.converter.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		entity.setKey(SystemProperty.ACCESS_JWT_ALGORITHM);
		entity.setValue("HMAC512");

		SystemPropertyEntity entity2 = new SystemPropertyEntity();
		entity2.setKey(null);
		entity2.setValue("HMAC512");

		// act
		SystemPropertyListDto response = converter.toDtoList(List.of(entity, entity2));
		
		// assert
		assertNotNull(response);
		assertFalse(response.getResultList().isEmpty());
		assertEquals(7L, response.getTotalElements());
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
		assertEquals(2L, response.getId());
		assertEquals(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS, response.getKey());
		assertEquals("1", response.getValue());
		assertEquals("2023-06-29T00:00Z", entity.getLastModified().toString());
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