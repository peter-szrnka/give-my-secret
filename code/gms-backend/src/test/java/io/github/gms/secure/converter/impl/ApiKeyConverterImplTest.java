package io.github.gms.secure.converter.impl;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.dto.ApiKeyListDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Unit test of {@link ApiKeyConverterImpl}
 * 
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
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToEntityWithParameters() {
		// act
		ApiKeyEntity entity = converter.toEntity(TestUtils.createApiKey(), TestUtils.createNewSaveApiKeyRequestDto());

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToNewEntity() {
		// arrange
		setupClock(clock);

		// act
		ApiKeyEntity entity = converter.toNewEntity(TestUtils.createNewSaveApiKeyRequestDto());

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToList() {
		// arrange
		Page<ApiKeyEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createApiKey()));

		// act
		ApiKeyListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
	}
}
