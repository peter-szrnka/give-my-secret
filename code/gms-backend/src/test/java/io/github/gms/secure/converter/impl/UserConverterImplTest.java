package io.github.gms.secure.converter.impl;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
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
		assertEquals("UserEntity(id=null, name=name, username=username, email=email@email.com, status=ACTIVE, credential=encoded, creationDate=null, roles=ROLE_USER, mfaEnabled=false, mfaSecret=null, failedAttempts=0)", entity.toString());
		verify(passwordEncoder).encode(anyString());
	}

	@Test
	void checkToEntityWithoutCredential() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
		dto.setId(1L);
		dto.setCredential(null);

		UserEntity existingEntity = TestUtils.createUser();
		existingEntity.setCreationDate(ZonedDateTime.now(clock));

		// act
		UserEntity entity = converter.toEntity(existingEntity, dto, false);

		// assert
		assertNotNull(entity);
		assertEquals("UserEntity(id=1, name=name, username=username, email=email@email.com, status=ACTIVE, credential=OldCredential, creationDate=2023-06-29T00:00Z, roles=ROLE_USER, mfaEnabled=false, mfaSecret=null, failedAttempts=0)", entity.toString());
	}

	@Test
	void checkToEntityWithParameters() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// act
		UserEntity mockEntity = TestUtils.createUser();
		mockEntity.setName("Test Test");
		mockEntity.setUsername("my-user-1");
		mockEntity.setCreationDate(ZonedDateTime.now(clock));
		mockEntity.setStatus(EntityStatus.DISABLED);
		mockEntity.setRoles("ROLE_VIEWER");
		UserEntity entity = converter.toEntity(mockEntity, dto, true);

		// assert
		assertNotNull(entity);
		assertEquals("UserEntity(id=null, name=name, username=username, email=email@email.com, status=ACTIVE, credential=encoded, creationDate=2023-06-29T00:00Z, roles=ROLE_USER, mfaEnabled=false, mfaSecret=null, failedAttempts=0)", entity.toString());
		verify(passwordEncoder).encode(anyString());
	}

	@Test
	void checkToNewEntity() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toNewEntity(dto, false);

		// assert
		assertNotNull(entity);
		assertEquals("UserEntity(id=null, name=name, username=username, email=email@email.com, status=ACTIVE, credential=encoded, creationDate=2023-06-29T00:00Z, roles=null, mfaEnabled=false, mfaSecret=null, failedAttempts=0)", entity.toString());
		verify(passwordEncoder).encode(anyString());
	}

	@Test
	void checkToNewEntityWithRoleChange() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		when(passwordEncoder.encode(anyString())).thenReturn("encoded");

		// arrange
		SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();

		// act
		UserEntity entity = converter.toNewEntity(dto, true);

		// assert
		assertNotNull(entity);
		assertEquals("UserEntity(id=null, name=name, username=username, email=email@email.com, status=ACTIVE, credential=encoded, creationDate=2023-06-29T00:00Z, roles=ROLE_USER, mfaEnabled=false, mfaSecret=null, failedAttempts=0)", entity.toString());
		verify(passwordEncoder).encode(anyString());
	}

	@Test
	void checkToList() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		UserEntity userEntity = TestUtils.createUser();
		userEntity.setCreationDate(ZonedDateTime.now(clock));
		Page<UserEntity> entityList = new PageImpl<>(Lists.newArrayList(userEntity));

		// act
		UserListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
		assertEquals(1L, resultList.getTotalElements());

		UserDto dto = resultList.getResultList().get(0);
		assertEquals(1L, dto.getId());
		assertEquals("name", dto.getName());
		assertEquals(TestUtils.USERNAME, dto.getUsername());

		assertEquals("a@b.com", dto.getEmail());
		assertEquals(EntityStatus.ACTIVE, dto.getStatus());
		assertEquals(1, dto.getRoles().size());
		assertEquals(UserRole.ROLE_USER, dto.getRoles().iterator().next());
		assertEquals("2023-06-29T00:00Z", dto.getCreationDate().toString());
	}

	@Test
	void checkToUserInfoDto() {
		// act
		UserInfoDto dto = converter.toUserInfoDto(TestUtils.createGmsUser(), false);

		// assert
		assertNotNull(dto);
		assertEquals(DemoData.USER_1_ID, dto.getId());
		assertEquals(DemoData.USERNAME1, dto.getName());
		assertEquals(DemoData.USERNAME1, dto.getUsername());
		assertEquals("a@b.com", dto.getEmail());
		assertEquals(1, dto.getRoles().size());
		assertEquals(UserRole.ROLE_USER, dto.getRoles().iterator().next());
	}

	@Test
	void checkToUserInfoDtoWithMfa() {
		// arrange
		GmsUserDetails testUser = TestUtils.createGmsUser();
		testUser.setMfaEnabled(true);
		testUser.setMfaSecret("secret");

		// act
		UserInfoDto dto = converter.toUserInfoDto(testUser, true);

		// assert
		assertNotNull(dto);
		assertEquals("UserInfoDto(id=null, name=null, username=username1, email=null, roles=[])", dto.toString());
	}
}
