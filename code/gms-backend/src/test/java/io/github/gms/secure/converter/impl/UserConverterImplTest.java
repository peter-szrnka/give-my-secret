package io.github.gms.secure.converter.impl;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link UserConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class UserConverterImplTest extends AbstractUnitTest {

	private Clock clock;
	private PasswordEncoder passwordEncoder;
	private UserConverterImpl converter;

	@BeforeEach
	void beforeEach() {
		clock = mock(Clock.class);
		passwordEncoder = mock(PasswordEncoder.class);
		converter = new UserConverterImpl(clock, passwordEncoder);
	}

	@Test
	void checkToEntityWithRoleChange() {
		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// act
		UserEntity entity = converter.toEntity(TestUtils.createUser(), dto, false);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		verify(passwordEncoder).encode(anyString());
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
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// act
		UserEntity entity = converter.toEntity(TestUtils.createUser(), dto, true);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		verify(passwordEncoder).encode(anyString());
	}
	
	@Test
	void checkToNewEntity() {
		// arrange
		setupClock(clock);
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toNewEntity(dto, false);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		verify(passwordEncoder).encode(anyString());
	}

	@Test
	void checkToNewEntityWithRoleChange() {
		// arrange
		setupClock(clock);
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toNewEntity(dto, true);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		verify(passwordEncoder).encode(anyString());
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

	@Test
	void checkToUserInfoDto() {
		// act
		UserInfoDto dto = converter.toUserInfoDto(TestUtils.createGmsUser());

		// assert
		assertNotNull(dto);
		assertEquals(DemoData.USER_1_ID, dto.getId());
		assertEquals(DemoData.USERNAME1, dto.getUsername());
	}
}
