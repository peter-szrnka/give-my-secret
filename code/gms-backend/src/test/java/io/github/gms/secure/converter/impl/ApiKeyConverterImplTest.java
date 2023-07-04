package io.github.gms.secure.converter.impl;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.dto.ApiKeyDto;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiKeyConverterImplTest extends AbstractUnitTest {

	private Clock clock;
	private ApiKeyConverterImpl converter;

	@BeforeEach
	void beforeEach() {
		// init
		clock = mock(Clock.class);
		converter = new ApiKeyConverterImpl(clock);
	}

	@Test
	void checkToEntityWithoutParameters() {
		// arrange
		SaveApiKeyRequestDto dto = TestUtils.createNewSaveApiKeyRequestDto();
		dto.setValue(null);
		dto.setStatus(null);
		
		// act
		ApiKeyEntity entity = converter.toEntity(TestUtils.createApiKey(), dto);

		// assert
		assertNotNull(entity);
		assertNull(entity.getId());
		assertEquals(1L, entity.getUserId());
		assertEquals("api-key-name", entity.getName());
		assertEquals("description2", entity.getDescription());
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToEntityWithParameters() {
		// arrange
		SaveApiKeyRequestDto dto = TestUtils.createNewSaveApiKeyRequestDto();
		dto.setId(3L);
		dto.setUserId(6L);
		ApiKeyEntity existingEntity = TestUtils.createApiKey();
		existingEntity.setValue("");
		existingEntity.setId(5L);
		existingEntity.setUserId(2L);
		existingEntity.setStatus(EntityStatus.DISABLED);

		// act
		ApiKeyEntity entity = converter.toEntity(existingEntity, dto);

		// assert
		assertNotNull(entity);
		assertEquals("ApiKeyEntity(id=3, userId=6, name=api-key-name, value=12345678, description=description2, status=ACTIVE, creationDate=null)", entity.toString());
	}

	@Test
	void checkToNewEntity() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SaveApiKeyRequestDto dto = TestUtils.createNewSaveApiKeyRequestDto();

		// act
		ApiKeyEntity entity = converter.toNewEntity(dto);

		// assert
		assertNotNull(entity);
		assertNull(entity.getId());
		assertEquals("api-key-name", entity.getName());
		assertEquals(1L, entity.getUserId());
		assertEquals("description2", entity.getDescription());
		assertEquals("12345678", entity.getValue());
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertEquals("2023-06-29T00:00Z", entity.getCreationDate().toString());
	}

	@Test
	void checkToList() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		ApiKeyEntity apiKeyEntity = TestUtils.createApiKey();
		apiKeyEntity.setCreationDate(ZonedDateTime.now(clock));
		Page<ApiKeyEntity> entityList = new PageImpl<>(Lists.newArrayList(apiKeyEntity));

		// act
		ApiKeyListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
		assertEquals(1L, resultList.getTotalElements());

		ApiKeyDto entity = resultList.getResultList().get(0);
		assertEquals(1L, entity.getId());
		assertEquals("test", entity.getName());
		assertEquals(1L, entity.getUserId());
		assertEquals("description", entity.getDescription());
		assertEquals("apikey", entity.getValue());
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertEquals("2023-06-29T00:00Z", entity.getCreationDate().toString());
	}
}
