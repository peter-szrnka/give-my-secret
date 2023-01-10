package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link UserConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class UserConverterImplTest extends AbstractUnitTest {
	
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserConverter converter = new UserConverterImpl();

	@Test
	void checkToEntityWithRoleChange() {
		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toEntity(TestUtils.createUser(), dto, false);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}
	
	@Test
	void checkToEntityWithoutCredential() {
		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
		dto.setCredential(null);

		// act
		UserEntity entity = converter.toEntity(TestUtils.createUser(), dto, false);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToEntityWithParameters() {
		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toEntity(TestUtils.createUser(), dto, true);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}
	
	@Test
	void checkToNewEntity() {
		// arrange
		setupClock();

		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toNewEntity(dto, false);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToNewEntityWithRoleChange() {
		// arrange
		setupClock();

		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toNewEntity(dto, true);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
	}

	@Test
	void checkToList() {
		// arrange
		Page<UserEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createUser()));

		// act
		UserListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
	}
}
