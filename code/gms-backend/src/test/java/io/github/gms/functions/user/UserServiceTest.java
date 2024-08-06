package io.github.gms.functions.user;

import ch.qos.logback.classic.Logger;
import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.service.JwtClaimService;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserServiceTest extends AbstractLoggingUnitTest {

	private UserRepository repository;
	private UserConverter converter;
	private PasswordEncoder passwordEncoder;
	private JwtClaimService jwtClaimService;
	private SecretGenerator secretGenerator;

	private UserService service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		repository = mock(UserRepository.class);
		converter = mock(UserConverter.class);
		passwordEncoder = mock(PasswordEncoder.class);
		jwtClaimService = mock(JwtClaimService.class);
		secretGenerator = mock(SecretGenerator.class);
		service = new UserService(repository, converter, passwordEncoder, jwtClaimService, secretGenerator);
		((Logger) LoggerFactory.getLogger(UserService.class)).addAppender(logAppender);
	}

	@Test
	void shouldSaveAdminUser() {
		// arrange
		when(converter.toNewEntity(any(SaveUserRequestDto.class), anyBoolean())).thenReturn(TestUtils.createUser());
		when(secretGenerator.generate()).thenReturn("secret!");
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createUser());

		// act
		SaveEntityResponseDto response = service.saveAdminUser(TestUtils.createSaveUserRequestDto());

		// assert
		assertNotNull(response);
		assertTrue(logAppender.list.stream()
				.anyMatch(log -> log.getFormattedMessage().contains("service saveUser called")));
		verify(converter).toNewEntity(any(SaveUserRequestDto.class), eq(true));
		ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(userEntityArgumentCaptor.capture());
		assertEquals("secret!", userEntityArgumentCaptor.getValue().getMfaSecret());
		verify(secretGenerator).generate();
	}

	@Test
	void shouldNotUpdateNonExistingUser() {
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		// act & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(1L)), "User entity not found!");
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void shouldUpdateExistingUser(boolean admin) {
		// arrange
		MDC.put(MdcParameter.IS_ADMIN.getDisplayName(), String.valueOf(admin));
		when(converter.toEntity(any(UserEntity.class), any(SaveUserRequestDto.class), eq(admin)))
				.thenReturn(TestUtils.createUser());
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createAdminUser());
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createAdminUser()));

		// act
		SaveEntityResponseDto response = service.save(TestUtils.createSaveUserRequestDto(1L));

		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(converter).toEntity(any(UserEntity.class), any(SaveUserRequestDto.class), eq(admin));
		verify(repository).save(any(UserEntity.class));
		MDC.remove(MdcParameter.IS_ADMIN.getDisplayName());
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
		when(converter.toDtoList(any())).thenReturn(UserListDto.builder()
				.resultList(Lists.newArrayList(TestUtils.createUserDto()))
				.totalElements(1).build());
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// act
		UserListDto response = service.list(pageable);

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
	@ValueSource(booleans = {true, false})
	void shouldToggleStatus(boolean enabled) {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		service.toggleStatus(1L, enabled);

		// assert
		ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(argumentCaptor.capture());

		assertEquals(enabled, argumentCaptor.getValue().getStatus() == EntityStatus.ACTIVE);
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
		when(repository.countNormalUsers()).thenReturn(3L);

		// act
		LongValueDto response = service.count();

		// assert
		assertEquals(3L, response.getValue());
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
		assertEquals("username", response);
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

	@Test
	@SneakyThrows
	void shouldReturnQrCodeUrl() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		UserEntity entity = TestUtils.createUser();
		entity.setEmail("szrnka.peter@gmail.com");
		entity.setMfaSecret("test");
		entity.setMfaEnabled(true);
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		// act
		byte[] response = service.getMfaQrCode();

		// assert
		assertNotNull(response);
		verify(repository).findById(1L);
		MDC.clear();
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void shouldToggleMfa(boolean value) {
		// arrange
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		service.toggleMfa(value);

		// assert
		ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(captor.capture());
		verify(repository).findById(1L);
		assertEquals(value, captor.getValue().isMfaEnabled());
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void shouldMfaActive(boolean value) {
		// arrange
		UserEntity entity = TestUtils.createUser();
		entity.setMfaEnabled(value);
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		// act
		boolean response = service.isMfaActive();

		// assert
		assertEquals(value, response);
		verify(repository).findById(1L);
	}

	@Test
	void shouldNotGetUserInfo() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(null);

		// act
		UserInfoDto response = service.getUserInfo(request);

		// assert
		assertNull(response);
		verify(jwtClaimService, never()).getClaims(anyString());
		verify(repository, never()).findById(1L);
	}

	@Test
	void shouldGetUserInfo() {
		// arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(List.of(new Cookie(ACCESS_JWT_TOKEN, "jwt")).toArray(new Cookie[1]));
		Claims claims = mock(Claims.class);
		when(claims.get(MdcParameter.USER_ID.getDisplayName(), Long.class)).thenReturn(1L);
		when(jwtClaimService.getClaims(anyString())).thenReturn(claims);
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		UserInfoDto response = service.getUserInfo(request);

		// assert
		assertNotNull(response);
		verify(jwtClaimService).getClaims(anyString());
		verify(repository).findById(1L);
	}
}
