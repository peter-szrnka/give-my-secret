package io.github.gms.functions.user;

import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.UserIdExtension;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.service.JwtClaimService;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.util.ThreadLocalContext;
import io.github.gms.util.TestUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
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

    //@RegisterExtension
    private final UserIdExtension userIdExtension = new UserIdExtension();

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
		// given
		when(converter.toNewEntity(any(SaveUserRequestDto.class), anyBoolean())).thenReturn(TestUtils.createUser());
		when(secretGenerator.generate()).thenReturn("secret!");
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createUser());

		// when
		SaveEntityResponseDto response = service.saveAdminUser(TestUtils.createSaveUserRequestDto());

		// then
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
        ThreadLocalContext.set(MdcParameter.IS_ADMIN, true);
        ThreadLocalContext.remove(MdcParameter.USER_ID);
		// given
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		// when & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(1L)), "User entity not found!");

        ThreadLocalContext.remove(MdcParameter.IS_ADMIN);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void save_whenUserAlreadyExists_thenSaveUser(boolean admin) {
		// given
		ThreadLocalContext.set(MdcParameter.IS_ADMIN, admin);
		when(converter.toEntity(any(UserEntity.class), any(SaveUserRequestDto.class), eq(admin)))
				.thenReturn(TestUtils.createUser());
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createAdminUser());
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createAdminUser()));

		// when
		SaveEntityResponseDto response = service.save(TestUtils.createSaveUserRequestDto(1L));

		// then
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		verify(converter).toEntity(any(UserEntity.class), any(SaveUserRequestDto.class), eq(admin));
		verify(repository).save(any(UserEntity.class));
        ThreadLocalContext.remove(MdcParameter.IS_ADMIN);
	}

	@Test
	void save_whenUserAlreadyExists_thenThrowException() {
        ThreadLocalContext.set(MdcParameter.IS_ADMIN, true);

		// given
		when(repository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(TestUtils.createAdminUser()));

		// when & assert
		TestUtils.assertGmsException(() -> service.save(TestUtils.createSaveUserRequestDto(null)), "User already exists!");
		verify(repository).findByUsernameOrEmail(anyString(), anyString());

        ThreadLocalContext.remove(MdcParameter.IS_ADMIN);
	}

	@Test
	void getById_whenEditorUserNotFound_thenThrowException() {
		// given
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// when
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

		// then
		assertEquals("User not found!", exception.getMessage());
		assertLogContains(logAppender, "User not found");
		verify(repository).findById(1L);
	}

	@Test
	void getById_whenUserNotFound_thenThrowException() {
		// given
		when(repository.findById(2L)).thenReturn(Optional.empty());

		// when
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(2L));

		// then
		assertEquals("User not found!", exception.getMessage());
		verify(repository).findById(2L);
	}

	@Test
	void getById_whenUserFound_thenReturnUserDto() {
		// given
		when(repository.findById(2L)).thenReturn(Optional.of(TestUtils.createUser()));
		when(converter.toDto(any(UserEntity.class))).thenReturn(TestUtils.createUserDto());
		// when
		UserDto response = service.getById(2L);

		// then
		assertNotNull(response);
		verify(repository).findById(2L);
		verify(converter).toDto(any(UserEntity.class));
	}

	@Test
	void list_whenUsersFound_thenReturnUserList() {
		// given
		Page<UserEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createUser()));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(UserListDto.builder()
				.resultList(Lists.newArrayList(TestUtils.createUserDto()))
				.totalElements(1).build());
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// when
		UserListDto response = service.list(pageable);

		// then
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(converter).toDtoList(any());
	}

	@Test
	void delete_whenUserFound_thenDeleteUser() {
		// given
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));

		// when
		service.delete(1L);

		// then
		verify(repository).findById(1L);
		verify(repository).deleteById(1L);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void toggleStatus_whenUserFound_thenToggleStatus(boolean enabled) {
		// given
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));

		// when
		service.toggleStatus(1L, enabled);

		// then
		ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(argumentCaptor.capture());

		assertEquals(enabled, argumentCaptor.getValue().getStatus() == EntityStatus.ACTIVE);
	}

	@Test
	void toggleStatus_whenUserNotFound_thenThrowException() {
		// given
		when(repository.findById(3L)).thenReturn(Optional.empty());

		// when
		GmsException exception = assertThrows(GmsException.class, () -> service.toggleStatus(3L, true));

		// then
		assertEquals("User not found!", exception.getMessage());
		verify(repository, never()).save(any());
	}

	@Test
	void count_whenQueried_thenReturnUserCount() {
		// given
		when(repository.countNormalUsers()).thenReturn(3L);

		// when
		LongValueDto response = service.count();

		// then
		assertEquals(3L, response.getValue());
		verify(repository).countNormalUsers();
	}

	@Test
	void getUsernameById_whenUserFound_thenReturnUsername() {
		// given
		when(repository.findById(2L)).thenReturn(Optional.of(TestUtils.createUser()));
		when(converter.toDto(any(UserEntity.class))).thenReturn(TestUtils.createUserDto());
		// when
		String response = service.getUsernameById(2L);

		// then
		assertNotNull(response);
		assertEquals("username", response);
		verify(repository).findById(2L);
		verify(converter).toDto(any(UserEntity.class));
	}

	@Test
	void changePassword_whenPasswordDoesNotMatch_thenThrowException() {
		// given
		when(passwordEncoder.matches(isNull(), anyString())).thenReturn(false);
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));

		// when & assert
		TestUtils.assertGmsException(() -> service.changePassword(new ChangePasswordRequestDto()), "Old credential is not valid!");
		verify(passwordEncoder).matches(isNull(), anyString());
	}

	@Test
	void changePassword_whenPasswordIsInvalid_thenThrowException() {
		// given
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));

		// when & assert
		TestUtils.assertGmsException(() -> service.changePassword(new ChangePasswordRequestDto("MyOldPassword", "MyNewPassword")), "New credential is not valid! It must contain at least 1 lowercase, 1 uppercase and 1 numeric character.");
		verify(passwordEncoder).matches(anyString(), anyString());
	}

	@Test
	void changePassword_whenCorrectInputProvided_thenChangePassword() {
		// given
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(passwordEncoder.encode(anyString())).thenReturn("MyEncodedPassword1!");

		// when & assert
		assertDoesNotThrow(() -> service.changePassword(new ChangePasswordRequestDto("MyOldPassword", "MyNewEncodedPassword2!")));

		ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(userEntityCaptor.capture());
		assertEquals("MyEncodedPassword1!", userEntityCaptor.getValue().getCredential());

		ArgumentCaptor<String> credentialCaptor = ArgumentCaptor.forClass(String.class);
		verify(passwordEncoder).encode(credentialCaptor.capture());
		verify(passwordEncoder).matches(anyString(), anyString());
		assertEquals("MyNewEncodedPassword2!", credentialCaptor.getValue());
	}

	@Test
	@SneakyThrows
	void getMfaQrCode_whenMfaIsEnabled_thenReturnImage() {
		// given
		UserEntity entity = TestUtils.createUser();
		entity.setEmail("john.doe@fictivehost.com");
		entity.setMfaSecret("test");
		entity.setMfaEnabled(true);
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		// when
		byte[] response = service.getMfaQrCode();

		// then
		assertNotNull(response);
		verify(repository).findById(1L);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void toggleMfa_whenValueProvided_thenUpdateMfaToggle(boolean value) {
		// given
		when(repository.findById(1L)).thenReturn(Optional.of(TestUtils.createUser()));

		// when
		service.toggleMfa(value);

		// then
		ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(captor.capture());
		verify(repository).findById(1L);
		assertEquals(value, captor.getValue().isMfaEnabled());
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void isMfaActive_whenMfaStatusIsDifferent_thenReturnResponse(boolean value) {
		// given
		UserEntity entity = TestUtils.createUser();
		entity.setMfaEnabled(value);
		when(repository.findById(1L)).thenReturn(Optional.of(entity));

		// when
		boolean response = service.isMfaActive();

		// then
		assertEquals(value, response);
		verify(repository).findById(1L);
	}

	@Test
	void getUserInfo_whenRequestDoesNotContainCookies_thenReturnUserInfo() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(null);

		// when
		UserInfoDto response = service.getUserInfo(request);

		// then
		assertNull(response);
		verify(jwtClaimService, never()).getClaims(anyString());
		verify(repository, never()).findById(1L);
	}

	@Test
	void getUserInfo_whenRequestContainsCookies_thenReturnUserInfo() {
		// given
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(List.of(new Cookie(ACCESS_JWT_TOKEN, "jwt")).toArray(new Cookie[1]));
		Claims claims = mock(Claims.class);
		when(claims.get(MdcParameter.USER_ID.getDisplayName(), Long.class)).thenReturn(1L);
		when(jwtClaimService.getClaims(anyString())).thenReturn(claims);
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));

		// when
		UserInfoDto response = service.getUserInfo(request);

		// then
		assertThat(response).hasToString("UserInfoDto(id=1, name=name, username=username, email=a@b.com, role=ROLE_USER, status=null, failedAttempts=null)");
		verify(jwtClaimService).getClaims(anyString());
		verify(repository).findById(1L);
	}
}

