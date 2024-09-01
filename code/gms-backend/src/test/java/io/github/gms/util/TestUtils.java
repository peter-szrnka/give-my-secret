package io.github.gms.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Sets;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.dto.LoginVerificationRequestDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.*;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.functions.announcement.AnnouncementDto;
import io.github.gms.functions.announcement.AnnouncementEntity;
import io.github.gms.functions.announcement.AnnouncementListDto;
import io.github.gms.functions.announcement.SaveAnnouncementDto;
import io.github.gms.functions.apikey.ApiKeyDto;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyListDto;
import io.github.gms.functions.apikey.SaveApiKeyRequestDto;
import io.github.gms.functions.event.EventDto;
import io.github.gms.functions.event.EventEntity;
import io.github.gms.functions.event.EventListDto;
import io.github.gms.functions.iprestriction.IpRestrictionDto;
import io.github.gms.functions.iprestriction.IpRestrictionEntity;
import io.github.gms.functions.iprestriction.IpRestrictionListDto;
import io.github.gms.functions.keystore.*;
import io.github.gms.functions.message.MessageDto;
import io.github.gms.functions.message.MessageEntity;
import io.github.gms.functions.message.MessageListDto;
import io.github.gms.functions.secret.*;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyEntity;
import io.github.gms.functions.systemproperty.SystemPropertyListDto;
import io.github.gms.functions.user.*;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.gms.common.enums.UserRole.ROLE_ADMIN;
import static io.github.gms.common.enums.UserRole.ROLE_USER;
import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.API_KEY_HEADER;
import static io.github.gms.util.DemoData.USER_3_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Szrnka
 */
@Slf4j
public class TestUtils {

	public static final String OLD_CREDENTIAL = "OldCredential";
	public static final String NEW_CREDENTIAL = "MyComplexPassword1!";
	public static final String EMAIL = "email@email.com";
	public static final String USERNAME = "username";
	public static final String USERNAME_MFA = "mfa_only";
	public static final String MOCK_ACCESS_TOKEN = "accessToken";
	public static final String MOCK_REFRESH_TOKEN = "refreshToken";
	public static final String LOCALHOST_8080 = "http://localhost:8080";

	public static ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		return mapper;
	}

	public static HttpHeaders getApiHttpHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (StringUtils.hasText(apiKey)) {
			headers.set(API_KEY_HEADER, apiKey);
		}

		return headers;
	}

	public static HttpHeaders getHttpHeaders(String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (jwt != null) {
			headers.add("Cookie", ACCESS_JWT_TOKEN + "=" + jwt + ";Max-Age=3600;HttpOnly");
		}

		return headers;
	}

	public static Cookie getCookie(String jwt) {
		Cookie cookie = new Cookie(ACCESS_JWT_TOKEN, jwt);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(3600);
		return cookie;
	}

	public static GmsUserDetails createGmsUser(Long userId, String username, String role) {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(userId)
				.username(username)
				.authorities(Sets.newHashSet(UserRole.valueOf(role)))
				.build();
	}

	public static GmsUserDetails createGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.enabled(true)
				.email("a@b.com")
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_1_ID)
				.name(DemoData.USERNAME1)
				.username(DemoData.USERNAME1)
				.authorities(Sets.newHashSet(ROLE_USER))
				.mfaSecret("MFA_SECRET")
				.build();
	}

	public static GmsUserDetails createBlockedGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.enabled(false)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_1_ID)
				.username(DemoData.USERNAME1)
				.authorities(Sets.newHashSet(ROLE_USER))
				.build();
	}

	public static GmsUserDetails createGmsAdminUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_2_ID)
				.username(DemoData.USERNAME2)
				.authorities(Sets.newHashSet(ROLE_ADMIN))
				.build();
	}

	public static GmsUserDetails createGmsMfaUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(USER_3_ID)
				.username(USERNAME_MFA)
				.authorities(Sets.newHashSet(ROLE_USER))
				.mfaEnabled(true)
				.mfaSecret("MFA_SECRET")
				.build();
	}

	public static SaveApiKeyRequestDto createSaveApiKeyRequestDto() {
		SaveApiKeyRequestDto request = new SaveApiKeyRequestDto();
		request.setId(1L);
		request.setName("api key 1");
		request.setUserId(1L);
		request.setValue("12345678");
		request.setStatus(EntityStatus.ACTIVE);
		return request;
	}

	public static SaveApiKeyRequestDto createNewSaveApiKeyRequestDto() {
		SaveApiKeyRequestDto request = new SaveApiKeyRequestDto();
		request.setName("api key 1");
		request.setUserId(1L);
		request.setValue("12345678");
		request.setStatus(EntityStatus.ACTIVE);
		request.setName("api-key-name");
		request.setDescription("description2");
		return request;
	}

	public static UserEntity createUser() {
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setName("name");
		user.setEmail("a@b.com");
		user.setRole(ROLE_USER);
		user.setStatus(EntityStatus.ACTIVE);
		user.setCredential(OLD_CREDENTIAL);
		user.setFailedAttempts(0);
		return user;
	}

	public static UserEntity createAdminUser() {
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setName("name");
		user.setRole(ROLE_ADMIN);
		user.setStatus(EntityStatus.ACTIVE);
		return user;
	}

	public static UserEntity createUserWithStatus(EntityStatus status) {
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setCredential(NEW_CREDENTIAL);
		user.setEmail("test@email.hu");
		user.setName("name");
		user.setRole(ROLE_USER);
		user.setStatus(status);
		return user;
	}

	public static UserEntity createMfaUser() {
		UserEntity user = new UserEntity();
		user.setId(USER_3_ID);
		user.setUsername(USERNAME_MFA);
		user.setCredential(NEW_CREDENTIAL);
		user.setEmail("test@email.hu");
		user.setName("name");
		user.setRole(ROLE_USER);
		user.setStatus(EntityStatus.ACTIVE);
		user.setMfaEnabled(true);
		user.setMfaSecret("MFA_SECRET");
		return user;
	}

	public static ApiKeyEntity createApiKey(Long id, String value) {
		ApiKeyEntity user = new ApiKeyEntity();
		user.setId(id);
		user.setUserId(1L);
		user.setStatus(EntityStatus.ACTIVE);
		user.setValue(value);
		return user;
	}

	public static ApiKeyEntity createApiKey() {
		ApiKeyEntity entity = new ApiKeyEntity();
		entity.setId(1L);
		entity.setName("test");
		entity.setUserId(1L);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue("apikey");
		entity.setDescription("description");
		return entity;
	}

	public static EventEntity createEventEntity() {
		EventEntity entity = new EventEntity();
		entity.setId(1L);
		entity.setEventDate(ZonedDateTime.now().minusHours(2));
		entity.setOperation(EventOperation.GET_BY_ID);
		entity.setTarget(EventTarget.KEYSTORE);
		entity.setUserId(DemoData.USER_1_ID);
		return entity;
	}

	public static SecretEntity createSecretEntity(Long id, Long keystoreAliasId, String secretId) {
		SecretEntity entity = new SecretEntity();
		entity.setId(id);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue(DemoData.ENCRYPTED_VALUE);
		entity.setType(SecretType.SIMPLE_CREDENTIAL);
		entity.setSecretId(secretId);
		entity.setKeystoreAliasId(keystoreAliasId);
		entity.setLastRotated(ZonedDateTime.now());
		entity.setReturnDecrypted(false);
		entity.setUserId(1L);
		return entity;
	}

	public static SecretEntity createSecretEntity() {
		return createSecretEntityWithUniqueKeystoreAliasId(DemoData.KEYSTORE_ALIAS_ID);
	}

	public static SecretEntity createSecretEntityWithUniqueKeystoreAliasId(Long keystoreAliasId) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setSecretId("secret");
		entity.setCreationDate(ZonedDateTime.now());
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue("test");
		entity.setKeystoreAliasId(keystoreAliasId);
		entity.setLastRotated(ZonedDateTime.now());
		entity.setUserId(1L);
		entity.setType(SecretType.SIMPLE_CREDENTIAL);
		return entity;
	}

	public static SecretEntity createSecretEntity(RotationPeriod rotationPeriod, ZonedDateTime lastRotated) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setRotationPeriod(rotationPeriod);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue("test");
		entity.setKeystoreAliasId(DemoData.KEYSTORE_ALIAS_ID);
		entity.setLastRotated(lastRotated);
		return entity;
	}

	public static SecretDto createSecretDto() {
		SecretDto dto = new SecretDto();
		dto.setId(1L);
		dto.setCreationDate(ZonedDateTime.now());
		dto.setRotationPeriod(RotationPeriod.YEARLY);
		dto.setStatus(EntityStatus.ACTIVE);
		return dto;
	}

	public static SaveKeystoreRequestDto createSaveKeystoreRequestDto(Long id) {
		SaveKeystoreRequestDto dto = new SaveKeystoreRequestDto();
		dto.setId(id);
		dto.setName("keystore");
		dto.setUserId(1L);
		dto.setCredential("test");
		dto.setStatus(EntityStatus.ACTIVE);
		dto.setType(KeystoreType.JKS);
		dto.setDescription("description");
		dto.setAliases(createKeystoreAliasList());
		return dto;
	}

	public static SaveKeystoreRequestDto createSaveKeystoreRequestDto() {
		return createSaveKeystoreRequestDto(DemoData.KEYSTORE_ID);
	}

	public static KeystoreEntity createKeystoreEntity() {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setName("keystore");
		entity.setId(DemoData.KEYSTORE_ID);
		entity.setFileName("test.jks");
		entity.setUserId(DemoData.USER_1_ID);
		entity.setCredential("test");
		entity.setType(KeystoreType.JKS);
		entity.setDescription("description");
		entity.setStatus(EntityStatus.ACTIVE);
		return entity;
	}

	public static KeystoreEntity createJKSKeystoreEntity() {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setId(DemoData.KEYSTORE_ID);
		entity.setFileName("test.jks");
		entity.setUserId(DemoData.USER_1_ID);
		entity.setCredential("test");
		entity.setType(KeystoreType.JKS);
		entity.setStatus(EntityStatus.ACTIVE);
		return entity;
	}

	public static KeystoreEntity createNewKeystoreEntity(Long id) {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setId(id);
		entity.setFileName("test.jks");
		entity.setUserId(DemoData.USER_1_ID);
		entity.setCredential("test");
		entity.setType(KeystoreType.JKS);
		entity.setDescription("description");
		entity.setStatus(EntityStatus.ACTIVE);
		return entity;
	}

	public static KeystoreAliasEntity createKeystoreAliasEntity(Long id, Long keystoreId) {
		KeystoreAliasEntity entity = new KeystoreAliasEntity();

		entity.setId(id);
		entity.setAlgorithm("algorithm");
		entity.setKeystoreId(keystoreId);
		entity.setAlias("test");
		entity.setAliasCredential("test");
		entity.setDescription("description");

		return entity;
	}

	public static KeystoreAliasEntity createKeystoreAliasEntity() {
		return createKeystoreAliasEntity(DemoData.KEYSTORE_ALIAS_ID, DemoData.KEYSTORE_ID);
	}

	public static IpRestrictionEntity createIpRestriction() {
		IpRestrictionEntity entity = new IpRestrictionEntity();
		entity.setId(1L);
		entity.setAllow(true);
		entity.setIpPattern(".*");
		entity.setSecretId(1L);
		entity.setUserId(1L);
		entity.setGlobal(false);
		entity.setStatus(EntityStatus.ACTIVE);
		return entity;
	}

	public static IpRestrictionDto createIpRestrictionDto() {
		IpRestrictionDto dto = new IpRestrictionDto();
		dto.setId(1L);
		dto.setAllow(true);
		dto.setIpPattern(".*");
		dto.setSecretId(1L);
		return dto;
	}

	public static IpRestrictionDto createIpRestrictionDto(boolean global) {
		IpRestrictionDto dto = new IpRestrictionDto();
		dto.setId(1L);
		dto.setAllow(true);
		dto.setIpPattern(".*");
		dto.setSecretId(1L);
		dto.setGlobal(global);
		return dto;
	}

    public static IpRestrictionListDto createIpRestrictionListDto() {
		return IpRestrictionListDto.builder()
				.totalElements(1)
				.resultList(List.of(createIpRestrictionDto()))
				.build();
    }

    public static ResponseCookie createResponseCookie(String cookieName) {
		return ResponseCookie.from(cookieName).value(null).build();
    }

    @Data
	@AllArgsConstructor
	public static class ValueHolder {
		KeyStoreValueType valueType;
		Long aliasId;
		String expectedValue;
	}

	public static SaveUserRequestDto createSaveUserRequestDto(Long id, String username, String email) {
		SaveUserRequestDto dto = new SaveUserRequestDto();
		dto.setId(id);
		dto.setUsername(username);
		dto.setName("name");
		dto.setEmail(email);
		dto.setRole(ROLE_USER);
		dto.setCredential(DemoData.CREDENTIAL_TEST);
		dto.setStatus(EntityStatus.ACTIVE);
		return dto;
	}

	public static SaveUserRequestDto createSaveUserRequestDtoWithNoRoles(String username, String email) {
		SaveUserRequestDto dto = new SaveUserRequestDto();
		dto.setId(null);
		dto.setUsername(username);
		dto.setName("name");
		dto.setEmail(email);
		dto.setCredential(DemoData.CREDENTIAL_TEST);
		dto.setStatus(EntityStatus.ACTIVE);
		return dto;
	}

	public static SaveUserRequestDto createSaveUserRequestDto(Long id) {
		return createSaveUserRequestDto(id, USERNAME, EMAIL);
	}

	public static SaveUserRequestDto createSaveUserRequestDto() {
		return createSaveUserRequestDto(null);
	}

	public static UserDto createUserDto() {
		UserDto user = new UserDto();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setName("name");
		user.setRole(ROLE_USER);
		user.setStatus(EntityStatus.ACTIVE);
		return user;
	}

	public static SaveSecretRequestDto createSaveSecretRequestDto(Long secretId, Set<Long> apiKeyRestrictionList) {
		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setId(secretId);
		dto.setRotationPeriod(RotationPeriod.YEARLY);
		dto.setStatus(EntityStatus.ACTIVE);
		dto.setType(SecretType.SIMPLE_CREDENTIAL);
		dto.setKeystoreId(DemoData.KEYSTORE_ID);
		dto.setKeystoreAliasId(DemoData.KEYSTORE_ALIAS_ID);
		dto.setSecretId(DemoData.SECRET_ID1);
		dto.setUserId(DemoData.USER_1_ID);
		dto.setValue(DemoData.SECRET_VALUE);
		dto.setRotationEnabled(true);
		dto.setReturnDecrypted(false);
		dto.setApiKeyRestrictions(apiKeyRestrictionList);
		return dto;
	}

	public static SaveSecretRequestDto createSaveSecretRequestDto(Long secretId) {
		return createSaveSecretRequestDto(secretId, new HashSet<>());
	}

	public static SaveSecretRequestDto createNewSaveSecretRequestDto() {
		return createSaveSecretRequestDto(null, new HashSet<>());
	}

	@SneakyThrows
	public static void createDirectory(String dir) {
		Path path = Paths.get(dir);
		deleteDirectoryWithContent(dir);
		Files.createDirectory(path);
	}

	@SneakyThrows
	public static void deleteDirectoryWithContent(String dir) {
		Path pathToBeDeleted = Paths.get(dir);

		if (!pathToBeDeleted.toFile().exists()) {
			return;
		}

		Files.walk(pathToBeDeleted)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(file -> {
					if (!file.exists()) {
						return;
					}

					log.info("file = " + file.getName());
					file.delete();
				});
	}

	public static AnnouncementEntity createAnnouncementEntity(Long id) {
		AnnouncementEntity entity = new AnnouncementEntity();
		entity.setId(id);
		entity.setAuthorId(1L);
		entity.setTitle("Maintenance at 2022-01-01");
		entity.setDescription("Test");
		entity.setAnnouncementDate(ZonedDateTime.now().minusDays(1));
		return entity;
	}

	public static SaveAnnouncementDto createSaveAnnouncementDto() {
		return SaveAnnouncementDto.builder()
				.author(DemoData.USERNAME1)
				.title("title")
				.description("description")
				.build();
	}

	public static ChangePasswordRequestDto createChangePasswordRequestDto() {
		return new ChangePasswordRequestDto("test", NEW_CREDENTIAL);
	}

	public static MessageEntity createMessageEntity() {
		return MessageEntity.builder().id(1L).message("test message").opened(false).userId(1L).build();
	}

	public static MessageEntity createNewMessageEntity() {
		return MessageEntity.builder().message("test message").opened(false).userId(1L).build();
	}

	public static ApiKeyRestrictionEntity createApiKeyRestrictionEntity(Long apiKeyId) {
		ApiKeyRestrictionEntity entity = new ApiKeyRestrictionEntity();
		entity.setId(1L);
		entity.setApiKeyId(apiKeyId);
		entity.setSecretId(1L);
		entity.setUserId(1L);
		return entity;
	}

	public static SecretEntity createMockSecret(String value, boolean returnDecrypted, SecretType type) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setValue(value);
		entity.setReturnDecrypted(returnDecrypted);
		entity.setKeystoreAliasId(1L);
		entity.setUserId(1L);
		entity.setKeystoreAliasId(1L);
		entity.setType(type);
		return entity;
	}

	public static void assertLogContains(ListAppender<ILoggingEvent> appender, String expectedMessage) {
		assertTrue(appender.list.stream().anyMatch(event -> event.getFormattedMessage().contains(expectedMessage)));
	}

	public static void assertLogMissing(ListAppender<ILoggingEvent> appender, String expectedMessage) {
		assertTrue(appender.list.stream().noneMatch(event -> event.getFormattedMessage().contains(expectedMessage)));
	}

	public static <T extends Exception> void assertException(Executable executable, String expectedMessage) {
		try {
			executable.execute();
		} catch (Throwable e) {
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	public static void assertGmsException(Executable executable, String expectedMessage) {
		assertException(executable, expectedMessage);
	}

	private static List<KeystoreAliasDto> createKeystoreAliasList() {
		KeystoreAliasDto dto = new KeystoreAliasDto();
		dto.setId(1L);
		dto.setAlias("test");
		dto.setAliasCredential("test");
		dto.setOperation(AliasOperation.SAVE);

		return Lists.newArrayList(dto);
	}

	public static SystemPropertyEntity createSystemPropertyEntity(SystemProperty key,
			String value) {
		SystemPropertyEntity entity = new SystemPropertyEntity();
		entity.setKey(key);
		entity.setValue(value);
		return entity;
	}

	public static GenerateJwtRequest createJwtAdminRequest() {
		return createJwtUserRequest(GmsUserDetails.builder()
				.userId(DemoData.USER_1_ID)
				.username(DemoData.USERNAME1)
				.authorities(Set.of(ROLE_ADMIN))
				.build());
	}

	public static GenerateJwtRequest createJwtUserRequest(GmsUserDetails user) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), user.getUserId(),
				MdcParameter.USER_NAME.getDisplayName(), user.getUsername(),
				"roles",
				user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));

		return GenerateJwtRequest.builder().subject(user.getUsername()).algorithm("HS512")
				.expirationDateInSeconds(30L)
				.claims(claims)
				.build();
	}

	public static GenerateJwtRequest createJwtUserRequest() {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), DemoData.USER_1_ID,
				MdcParameter.USER_NAME.getDisplayName(), DemoData.USERNAME1,
				"roles", List.of("ROLE_USER"));

		return GenerateJwtRequest.builder().subject(DemoData.USERNAME1).algorithm("HS512")
				.expirationDateInSeconds(30L)
				.claims(claims)
				.build();
	}

	public static ApiKeyDto createApiKeyDto() {
		ApiKeyDto dto = new ApiKeyDto();
		dto.setId(1L);
		dto.setName("api-key-1");
		dto.setValue("value");
		dto.setStatus(EntityStatus.ACTIVE);
		dto.setDescription("test");
		return dto;
	}

	public static ApiKeyListDto createApiKeyListDto() {
		return new ApiKeyListDto(List.of(createApiKeyDto()), 1);
	}

	public static KeystoreDto createKeystoreDto() {
		KeystoreDto dto = new KeystoreDto();
		dto.setId(1L);
		dto.setName("my-jks-1");
		dto.setType(KeystoreType.JKS);
		dto.setStatus(EntityStatus.ACTIVE);
		return dto;
	}

	public static KeystoreListDto createKeystoreListDto() {
		return new KeystoreListDto(List.of(createKeystoreDto()), 1);
	}

	public static UserListDto createUserListDto() {
		return UserListDto.builder().resultList(List.of(createUserDto())).totalElements(1L).build();
	}

	public static AnnouncementDto createAnnouncementDto() {
		return AnnouncementDto.builder()
				.id(1L)
				.author(DemoData.USERNAME1)
				.title("title")
				.description("description")
				.build();
	}

    public static AnnouncementListDto createAnnouncementListDto() {
        return AnnouncementListDto.builder().resultList(List.of(createAnnouncementDto())).totalElements(1L).build();
    }

    public static SystemPropertyDto createSystemPropertyDto() {
		return SystemPropertyDto.builder()
			.key("key")
			.value("value")
			.factoryValue(false)
			.build();
    }

    public static SystemPropertyListDto createSystemPropertyListDto() {
        return SystemPropertyListDto.builder().resultList(List.of(createSystemPropertyDto())).totalElements(1L).build();
    }

    public static EventListDto createEventListDto() {
        return EventListDto.builder().resultList(List.of(new EventDto())).totalElements(1L).build();
    }

    public static SecretListDto createSecretListDto() {
        return SecretListDto.builder().resultList(List.of(createSecretDto())).totalElements(1L).build();
    }

    public static MessageListDto createMessageListDto() {
        return MessageListDto.builder().resultList(List.of(MessageDto.builder()
			.id(1L)
			.message("message")
			.userId(1L)
			.opened(false)
			.creationDate(ZonedDateTime.now().minusDays(1))
			.build())).build();
    }

    public static UserInfoDto createUserInfoDto() {
		UserInfoDto dto = new UserInfoDto();
		dto.setId(1L);
		dto.setUsername("user");
		dto.setName("name");
		dto.setRole(ROLE_USER);
		dto.setEmail("a@b.com");
		dto.setStatus(EntityStatus.ACTIVE);
        return dto;
    }

    public static LoginVerificationRequestDto createLoginVerificationRequestDto() {
		LoginVerificationRequestDto dto = new LoginVerificationRequestDto();
		dto.setUsername("user1");
		dto.setVerificationCode("123456");
        return dto;
    }
}
