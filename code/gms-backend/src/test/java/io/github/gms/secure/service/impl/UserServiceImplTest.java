package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.entity.UserEntity;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link UserServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class UserServiceImplTest extends AbstractLoggingUnitTest {

	@Mock
	private UserRepository repository;

	@Mock
	private UserConverter converter;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		((Logger) LoggerFactory.getLogger(UserServiceImpl.class)).addAppender(logAppender);
	}

	@Test
	void shouldSaveAdminUser() {
		// arrange
		when(converter.toNewEntity(any(SaveUserRequestDto.class), anyBoolean())).thenReturn(TestUtils.createUser());
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createUser());

		// act
		service.saveAdminUser(TestUtils.createSaveUserRequestDto());

		// assert
		assertTrue(logAppender.list.stream()
				.anyMatch(log -> log.getFormattedMessage().contains("service saveUser called")));
		verify(converter).toNewEntity(any(SaveUserRequestDto.class), anyBoolean());
		verify(repository).save(any(UserEntity.class));
	}

	@Test
	void shouldNotUpdateNonExistingUser() {
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		// act & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(1L)), "User entity not found!");
	}

	@Test
	void shouldUpdateExistingUser() {
		// arrange
		when(converter.toEntity(any(UserEntity.class), any(SaveUserRequestDto.class), anyBoolean()))
				.thenReturn(TestUtils.createUser());
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createAdminUser());
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createAdminUser()));

		// act
		service.save(TestUtils.createSaveUserRequestDto(1L));

		// assert
		verify(converter).toEntity(any(UserEntity.class), any(SaveUserRequestDto.class), anyBoolean());
		verify(repository).save(any(UserEntity.class));
	}

	@Test
	void shouldNotSaveExistingUser() {
		// arrange
		when(repository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(TestUtils.createAdminUser()));

		// act & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(null)), "User already exists!");
		verify(repository).findByUsernameOrEmail(anyString(), anyString());
	}
	
	@Test
	void shouldNotFindEditorUserById() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

		// assert
		assertEquals("User not found!", exception.getMessage());
		assertTrue(logAppender.list.stream()
				.anyMatch(log -> log.getFormattedMessage().contains("User not found")));
		verify(repository).findById(1L);
	}
	
	@Test
	void shouldNotFindUserById() {
		// arrange
		when(repository.findById(2L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(2L));

		// assert
		assertEquals("User not found!", exception.getMessage());
		verify(repository).findById(2L);
	}
	
	@Test
	void shouldFindUserById() {
		// arrange
		when(repository.findById(2L)).thenReturn(Optional.of(TestUtils.createUser()));
		when(converter.toDto(any(UserEntity.class))).thenReturn(TestUtils.createUserDto());
		// act
		UserDto response = service.getById(2L);

		// assert
		assertNotNull(response);
		verify(repository).findById(2L);
		verify(converter).toDto(any(UserEntity.class));
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		Page<UserEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createUser()));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(new UserListDto(Lists.newArrayList(TestUtils.createUserDto())));

		// act
		UserListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(converter).toDtoList(any());
	}
	
	@Test
	void shouldDelete() {
		// arrange
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		service.delete(1L);

		// assert
		verify(repository).findById(1L);
		verify(repository).deleteById(1L);
	}
	
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldToggleStatus(boolean enabled) {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		service.toggleStatus(1L, enabled);

		// assert
		verify(repository).save(any());
	}
	
	@Test
	void shouldNotToggleStatus() {
		// arrange
		when(repository.findById(3L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.toggleStatus(3L, true));

		// assert
		assertEquals("User not found!", exception.getMessage());
		verify(repository, never()).save(any());
	}
	
	@Test
	void shouldReturnUserCount() {
		// arrange
		when(repository.countNormalUsers()).thenReturn(3l);
		
		// act
		LongValueDto response = service.count();
		
		// assert
		assertEquals(3l, response.getValue());
		verify(repository).countNormalUsers();
	}
	
	@Test
	void shouldGetUsernameById() {
		// arrange
		when(repository.findById(2L)).thenReturn(Optional.of(TestUtils.createUser()));
		when(converter.toDto(any(UserEntity.class))).thenReturn(TestUtils.createUserDto());
		// act
		String response = service.getUsernameById(2L);

		// assert
		assertNotNull(response);
		verify(repository).findById(2L);
		verify(converter).toDto(any(UserEntity.class));
	}
	
	@Test
	void shouldNotSaveNewEmptyPassword() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		when(passwordEncoder.matches(isNull(), anyString())).thenReturn(false);
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));
		
		// act & assert
		TestUtils.assertGmsException(() -> service.changePassword(new ChangePasswordRequestDto()), "Old credential is not valid!");
		verify(passwordEncoder).matches(isNull(), anyString());
		MDC.clear();
	}
	
	@Test
	void shouldNotSaveNewInvalidPassword() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));
		
		// act & assert
		TestUtils.assertGmsException(() -> service.changePassword(new ChangePasswordRequestDto("MyOldPassword", "MyNewPassword")), "New credential is not valid! It must contain at least 1 lowercase, 1 uppercase and 1 numeric character.");
		verify(passwordEncoder).matches(anyString(), anyString());
		MDC.clear();
	}
	
	@Test
	void shouldSaveNewPassword() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(passwordEncoder.encode(anyString())).thenReturn("MyEncodedPassword1!");
		
		// act & assert
		assertDoesNotThrow(() -> service.changePassword(new ChangePasswordRequestDto("MyOldPassword", "MyNewEncodedPassword2!")));
		
		ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(userEntityCaptor.capture());
		assertEquals("MyEncodedPassword1!", userEntityCaptor.getValue().getCredential());
		
		ArgumentCaptor<String> credentialCaptor = ArgumentCaptor.forClass(String.class);
		verify(passwordEncoder).encode(credentialCaptor.capture());
		verify(passwordEncoder).matches(anyString(), anyString());
		assertEquals("MyNewEncodedPassword2!", credentialCaptor.getValue());

		MDC.clear();
	}
}
