package io.github.gms.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.function.Executable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.entity.AnnouncementEntity;
import io.github.gms.common.entity.ApiKeyEntity;
import io.github.gms.common.entity.ApiKeyRestrictionEntity;
import io.github.gms.common.entity.EventEntity;
import io.github.gms.common.entity.KeystoreEntity;
import io.github.gms.common.entity.MessageEntity;
import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.entity.UserEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.KeystoreType;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 */
@Slf4j
public class TestUtils {
	
	public static final String OLD_CREDENTIAL = "OldCredential";
	public static final String NEW_CREDENTIAL = "MyComplexPassword1!";
	private static final String EMAIL = "email@email.com";
	private static final String USERNAME = "username";
	
	public static Gson getGson() {
		return new GsonBuilder()
				.serializeNulls()
				.enableComplexMapKeySerialization()
				.setLenient()
				.registerTypeAdapter(LocalDateTime.class,
						(JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
				.registerTypeAdapter(LocalDateTime.class,
						(JsonSerializer<LocalDateTime>) (localDateTime, typeOfT, context) -> new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime)))
				.create();
	}

	public static HttpHeaders getApiHttpHeaders(String apiKey, String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-API-KEY", apiKey);
		headers.add("Cookie", Constants.JWT_TOKEN + "=" + jwt + ";Max-Age=3600");
		
		return headers;
	}
	
	public static HttpHeaders getHttpHeaders(String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);	

		if (jwt != null) {
			headers.add("Cookie", Constants.JWT_TOKEN + "=" + jwt + ";Max-Age=3600;HttpOnly");
		}

		return headers;
	}
	
	public static Cookie getCookie(String jwt) {
		Cookie cookie = new Cookie(Constants.JWT_TOKEN, jwt);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(3600);
		return cookie;
	}
	
	public static GmsUserDetails createGmsUser(Long userId, String username, String role) {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.credential(DemoDataProviderService.CREDENTIAL_TEST)
				.userId(userId)
				.username(username)
				.authorities(Sets.newHashSet(UserRole.valueOf(role)))
				.build();
	}

	public static GmsUserDetails createGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.enabled(true)
				.credential(DemoDataProviderService.CREDENTIAL_TEST)
				.userId(DemoDataProviderService.USER_1_ID)
				.username(DemoDataProviderService.USERNAME1)
				.authorities(Sets.newHashSet(UserRole.ROLE_USER))
				.build();
	}
	
	public static GmsUserDetails createBlockedGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.enabled(false)
				.credential(DemoDataProviderService.CREDENTIAL_TEST)
				.userId(DemoDataProviderService.USER_1_ID)
				.username(DemoDataProviderService.USERNAME1)
				.authorities(Sets.newHashSet(UserRole.ROLE_USER))
				.build();
	}
	
	public static GmsUserDetails createLockedGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(false)
				.enabled(true)
				.credential(DemoDataProviderService.CREDENTIAL_TEST)
				.userId(DemoDataProviderService.USER_1_ID)
				.username(DemoDataProviderService.USERNAME1)
				.authorities(Sets.newHashSet(UserRole.ROLE_USER))
				.build();
	}
	
	public static GmsUserDetails createGmsAdminUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.credential(DemoDataProviderService.CREDENTIAL_TEST)
				.userId(DemoDataProviderService.USER_2_ID)
				.username(DemoDataProviderService.USERNAME2)
				.authorities(Sets.newHashSet(UserRole.ROLE_ADMIN))
				.build();
	}

	public static SaveApiKeyRequestDto createSaveApiKeyRequestDto() {
		SaveApiKeyRequestDto request = new SaveApiKeyRequestDto();
		request.setId(1L);
		request.setUserId(1L);
		request.setValue("12345678");
		request.setStatus(EntityStatus.ACTIVE);
		return request;
	}
	
	public static SaveApiKeyRequestDto createNewSaveApiKeyRequestDto() {
		SaveApiKeyRequestDto request = new SaveApiKeyRequestDto();
		request.setUserId(1L);
		request.setValue("12345678");
		request.setStatus(EntityStatus.ACTIVE);
		return request;
	}

	public static UserEntity createUser() {
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setName("name");
		user.setRoles("ROLE_USER");
		user.setStatus(EntityStatus.ACTIVE);
		user.setCredential(OLD_CREDENTIAL);
		return user;
	}
	
	public static UserEntity createAdminUser() {
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setName("name");
		user.setRoles("ROLE_ADMIN");
		user.setStatus(EntityStatus.ACTIVE);
		return user;
	}
	
	public static UserEntity createUserWithStatus(EntityStatus status) {
		UserEntity user = new UserEntity();
		user.setId(1L);
		user.setUsername(USERNAME);
		user.setName("name");
		user.setRoles("ROLE_USER");
		user.setStatus(status);
		return user;
	}

	public static ApiKeyEntity createApiKey() {
		ApiKeyEntity user = new ApiKeyEntity();
		user.setId(1L);
		user.setUserId(1L);
		user.setStatus(EntityStatus.ACTIVE);
		user.setValue("apikey");
		return user;
	}

	public static EventEntity createEventEntity() {
		EventEntity entity = new EventEntity();
		entity.setId(1L);
		entity.setEventDate(LocalDateTime.now().minusHours(2));
		entity.setOperation(EventOperation.GET_BY_ID);
		entity.setTarget(EventTarget.KEYSTORE);
		entity.setUserId("user");
		return entity;
	}

	public static SecretEntity createSecretEntity() {
		return createSecretEntityWithUniqueKeystoreId(DemoDataProviderService.KEYSTORE_ID);
	}
	
	public static SecretEntity createSecretEntityWithUniqueKeystoreId(Long keystoreId) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setCreationDate(LocalDateTime.now());
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue("test");
		entity.setKeystoreId(keystoreId);
		entity.setLastRotated(LocalDateTime.now());
		entity.setUserId(1L);
		return entity;
	}
	
	public static SecretEntity createSecretEntity(RotationPeriod rotationPeriod, LocalDateTime lastRotated) {
		SecretEntity entity = new SecretEntity();
		entity.setId(1L);
		entity.setCreationDate(LocalDateTime.now());
		entity.setRotationPeriod(rotationPeriod);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue("test");
		entity.setKeystoreId(DemoDataProviderService.KEYSTORE_ID);
		entity.setLastRotated(lastRotated);
		return entity;
	}

	public static SecretDto createSecretDto() {
		SecretDto dto = new SecretDto();
		dto.setId(1L);
		dto.setCreationDate(LocalDateTime.now());
		dto.setRotationPeriod(RotationPeriod.YEARLY);
		dto.setStatus(EntityStatus.ACTIVE);
		return dto;
	}

	public static SaveKeystoreRequestDto createSaveKeystoreRequestDto() {
		SaveKeystoreRequestDto dto = new SaveKeystoreRequestDto();
		dto.setName("keystore");
		dto.setUserId(1L);
		dto.setAlias("test");
		dto.setAliasCredential("test");
		dto.setCredential("test");
		dto.setStatus(EntityStatus.ACTIVE);
		dto.setType(KeystoreType.JKS);
		dto.setDescription("description");
		return dto;
	}

	public static KeystoreEntity createKeystoreEntity() {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setName("keystore");
		entity.setId(DemoDataProviderService.KEYSTORE_ID);
		entity.setFileName("test.jks");
		entity.setUserId(DemoDataProviderService.USER_1_ID);
		entity.setAlias("test");
		entity.setAliasCredential("test");
		entity.setCredential("test");
		entity.setType(KeystoreType.JKS);
		entity.setDescription("description");
		return entity;
	}
	
	public static KeystoreEntity createJKSKeystoreEntity() {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setId(DemoDataProviderService.KEYSTORE_ID);
		entity.setFileName("test.jks");
		entity.setUserId(DemoDataProviderService.USER_1_ID);
		entity.setAlias("test");
		entity.setAliasCredential("test");
		entity.setCredential("test");
		entity.setType(KeystoreType.JKS);
		return entity;
	}
	
	public static KeystoreEntity createNewKeystoreEntity(Long id) {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setId(id);
		entity.setFileName("test.jks");
		entity.setUserId(DemoDataProviderService.USER_1_ID);
		entity.setAlias("test");
		entity.setAliasCredential("test");
		entity.setCredential("test");
		entity.setType(KeystoreType.JKS);
		return entity;
	}
	
	@Data
	@AllArgsConstructor
	public static class ValueHolder {
		KeyStoreValueType valueType;
		String expectedValue;
	}
	
	public static SaveUserRequestDto createSaveUserRequestDto(Long id, String username, String email) {
		SaveUserRequestDto dto = new SaveUserRequestDto();
		dto.setId(id);
		dto.setUsername(username);
		dto.setName("name");
		dto.setEmail(email);
		dto.setRoles(Sets.newHashSet(UserRole.ROLE_USER));
		dto.setCredential(DemoDataProviderService.CREDENTIAL_TEST);
		dto.setStatus(EntityStatus.ACTIVE);
		return dto;
	}
	
	public static SaveUserRequestDto createSaveUserRequestDtoWithNoRoles(String username, String email) {
		SaveUserRequestDto dto = new SaveUserRequestDto();
		dto.setId(null);
		dto.setUsername(username);
		dto.setName("name");
		dto.setEmail(email);
		dto.setCredential(DemoDataProviderService.CREDENTIAL_TEST);
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
		user.setRoles(Sets.newHashSet(UserRole.ROLE_USER));
		user.setStatus(EntityStatus.ACTIVE);
		return user;
	}
	
	public static SaveSecretRequestDto createSaveSecretRequestDto(Long secretId, Set<Long> apiKeyRestrictionList) {
		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setId(secretId);
		dto.setRotationPeriod(RotationPeriod.YEARLY);
		dto.setStatus(EntityStatus.ACTIVE);
		dto.setKeystoreId(DemoDataProviderService.KEYSTORE_ID);
		dto.setSecretId(DemoDataProviderService.SECRET_ID1);
		dto.setUserId(DemoDataProviderService.USER_1_ID);
		dto.setValue(DemoDataProviderService.SECRET_VALUE);
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
	public static void deleteDirectoryWithContent(String dir) {
		Path pathToBeDeleted = Paths.get(dir);

	    Files.walk(pathToBeDeleted)
	      .sorted(Comparator.reverseOrder())
	      .map(Path::toFile)
	      .forEach(file -> {
	    	  log.info("file = " + file.getName());
	    	  file.delete();
	      });
	    		  //File::delete);
	}

	public static Page<AnnouncementEntity> createAnnouncementEntityList() {
		AnnouncementEntity entity1 = new AnnouncementEntity();
		entity1.setId(1L);
		entity1.setAuthorId(1L);
		entity1.setTitle("Maintenance at 2022-01-01");
		entity1.setDescription("Test");
		entity1.setAnnouncementDate(LocalDateTime.now().minusDays(1));
		return new PageImpl<AnnouncementEntity>(Lists.newArrayList(entity1));
	}

	public static AnnouncementEntity createAnnouncementEntity(Long id) {
		AnnouncementEntity entity = new AnnouncementEntity();
		entity.setId(id);
		entity.setAuthorId(1L);
		entity.setTitle("Maintenance at 2022-01-01");
		entity.setDescription("Test");
		entity.setAnnouncementDate(LocalDateTime.now().minusDays(1));
		return entity;
	}

	public static SaveAnnouncementDto createSaveAnnouncementDto() {
		return SaveAnnouncementDto.builder()
				.author(DemoDataProviderService.USERNAME1)
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
	
	public static void assertLogContains(ListAppender<ILoggingEvent> appender, String expectedMessage) {
		assertTrue(appender.list.stream().anyMatch(event -> event.getFormattedMessage().contains(expectedMessage)));
	}
	
	public static <T extends Exception> void assertException(Class<T> cls, Executable executable, String expectedMessage) {
		try {
			executable.execute();
		} catch(Throwable e) {
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	public static void assertGmsException(Executable executable, String expectedMessage) {
		assertException(GmsException.class, executable, expectedMessage);
	}
}
