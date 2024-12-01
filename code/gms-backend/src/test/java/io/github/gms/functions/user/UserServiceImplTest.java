package io.github.gms.functions.user;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserServiceImplTest extends AbstractLoggingUnitTest {

	private UserRepository repository;
	private UserConverter converter;
	private PasswordEncoder passwordEncoder;
	private JwtClaimService jwtClaimService;
	private SecretGenerator secretGenerator;

	private UserServiceImpl service;

	@Override
	@BeforeEach
	public void setup() {
		super.setup();
		repository = mock(UserRepository.class);
		converter = mock(UserConverter.class);
		passwordEncoder = mock(PasswordEncoder.class);
		jwtClaimService = mock(JwtClaimService.class);
		secretGenerator = mock(SecretGenerator.class);
		service = new UserServiceImpl(repository, converter, passwordEncoder, jwtClaimService, secretGenerator);
		addAppender(UserServiceImpl.class);
	}

	@Test
	void save_whenInputUserIsAdmin_thenReturnSave() {
		// arrange
		when(converter.toNewEntity(any(SaveUserRequestDto.class), anyBoolean())).thenReturn(TestUtils.createUser());
		when(secretGenerator.generate()).thenReturn("secret!");
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createUser());

		// act
		SaveEntityResponseDto response = service.saveAdminUser(TestUtils.createSaveUserRequestDto());

		// assert
		assertNotNull(response);
		assertLogContains(logAppender, "service saveUser called");
		verify(converter).toNewEntity(any(SaveUserRequestDto.class), eq(true));
		ArgumentCaptor<UserEntity> userEntityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(userEntityArgumentCaptor.capture());
		assertEquals("secret!", userEntityArgumentCaptor.getValue().getMfaSecret());
		verify(secretGenerator).generate();
	}

	@Test
	void save_whenUserNotFound_thenThrowException() {
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		// act & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(1L)), "User entity not found!");
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void save_whenUserAlreadyExists_thenSaveUser(boolean admin) {
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
	void save_whenUserAlreadyExists_thenThrowException() {
		// arrange
		when(repository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(TestUtils.createAdminUser()));

		// act & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(null)), "User already exists!");
		verify(repository).findByUsernameOrEmail(anyString(), anyString());
	}

	@Test
	void getById_whenEditorUserNotFound_thenThrowException() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

		// assert
		assertEquals("User not found!", exception.getMessage());
		assertLogContains(logAppender, "User not found");
		verify(repository).findById(1L);
	}

	@Test
	void getById_whenUserNotFound_thenThrowException() {
		// arrange
		when(repository.findById(2L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(2L));

		// assert
		assertEquals("User not found!", exception.getMessage());
		verify(repository).findById(2L);
	}

	@Test
	void getById_whenUserFound_thenReturnUserDto() {
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
	void list_whenUsersFound_thenReturnUserList() {
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
	void delete_whenUserFound_thenDeleteUser() {
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
	void toggleStatus_whenUserFound_thenToggleStatus(boolean enabled) {
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
	void toggleStatus_whenUserNotFound_thenThrowException() {
		// arrange
		when(repository.findById(3L)).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.toggleStatus(3L, true));

		// assert
		assertEquals("User not found!", exception.getMessage());
		verify(repository, never()).save(any());
	}

	@Test
	void count_whenQueried_thenReturnUserCount() {
		// arrange
		when(repository.countNormalUsers()).thenReturn(3L);

		// act
		LongValueDto response = service.count();

		// assert
		assertEquals(3L, response.getValue());
		verify(repository).countNormalUsers();
	}

	@Test
	void getUsernameById_whenUserFound_thenReturnUsername() {
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
	void changePassword_whenPasswordDoesNotMatch_thenThrowException() {
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
	void changePassword_whenPasswordIsInvalid_thenThrowException() {
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
	void changePassword_whenCorrectInputProvided_thenChangePassword() {
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
	void getMfaQrCode_whenMfaIsEnabled_thenReturnImage() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		UserEntity entity = TestUtils.createUser();
		entity.setEmail("john.doe@fictivehost.com");
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
	void toggleMfa_whenValueProvided_thenUpdateMfaToggle(boolean value) {
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
	void isMfaActive_whenMfaStatusIsDifferent_thenReturnResponse(boolean value) {
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
	void getUserInfo_whenRequestDoesNotContainCookies_thenReturnUserInfo() {
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
	void getUserInfo_whenRequestContainsCookies_thenReturnUserInfo() {
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
		assertThat(response).hasToString("UserInfoDto(id=1, name=name, username=username, email=a@b.com, role=ROLE_USER, status=null, failedAttempts=null)");
		verify(jwtClaimService).getClaims(anyString());
		verify(repository).findById(1L);
	}
}
