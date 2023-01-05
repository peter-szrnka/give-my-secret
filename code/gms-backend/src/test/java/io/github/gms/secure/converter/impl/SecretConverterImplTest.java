package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link SecretConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretConverterImplTest extends AbstractUnitTest {

	@InjectMocks
	private SecretConverterImpl converter;

	@Test
	void checkToEntityWithoutParameters() {
		// arrange
		setupClock();

		SecretEntity entity = converter.toEntity(TestUtils.createSecretEntity(), new SaveSecretRequestDto());

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToEntityWithParameters() {
		// arrange
		setupClock();

		// arrange
		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setValue("value");
		dto.setRotationPeriod(RotationPeriod.DAILY);
		dto.setStatus(EntityStatus.DISABLED);

		// act
		SecretEntity entity = converter.toEntity(TestUtils.createSecretEntity(), dto);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.DISABLED, entity.getStatus());
	}

	@Test
	void checkToNewEntity() {
		// arrange
		setupClock();

		// arrange
		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setValue("value");
		dto.setRotationPeriod(RotationPeriod.DAILY);

		// act
		SecretEntity entity = converter.toNewEntity(dto);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToList() {
		// arrange
		Page<SecretEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createSecretEntity()));

		// act
		SecretListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
	}
}
