package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;

/**
 * Unit test of {@link SystemPropertyConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemPropertyConverterImplTest extends AbstractUnitTest {

	private SystemPropertyConverterImpl converter = new SystemPropertyConverterImpl(Clock.systemDefaultZone());

	@Test
	void shouldConvertToDtoList() {
		// arrange
		SystemPropertyEntity entity = new SystemPropertyEntity();
		entity.setKey(SystemProperty.ACCESS_JWT_ALGORITHM);
		entity.setValue("HMAC512");

		// act
		SystemPropertyListDto response = converter.toDtoList(List.of(entity));
		
		// assert
		assertNotNull(response);
		assertFalse(response.getResultList().isEmpty());
	}
	
	@Test
	void shouldConvertToNewEntity() {
		// act
		SystemPropertyEntity response = converter.toEntity(null, SystemPropertyDto.builder().key(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS.name()).value("1").build());
		
		// assert
		assertNotNull(response);
		assertEquals(SystemProperty.OLD_EVENT_TIME_LIMIT_DAYS, response.getKey());
		assertEquals("1", response.getValue());
	}
	
	@Test
	void shouldNotConvertToExistingEntity() {
		// act
		GmsException exception = assertThrows(GmsException.class, () -> converter.toEntity(new SystemPropertyEntity(), SystemPropertyDto.builder().key("invalid").value("1").build()));
		
		// assert
		assertEquals("Unknown system property!", exception.getMessage());
	}
}