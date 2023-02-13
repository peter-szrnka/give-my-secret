package io.github.gms.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.function.Executable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Sets;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.api.controller.ApiController;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.KeystoreType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.KeystoreAliasDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveApiKeyRequestDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.entity.AnnouncementEntity;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.EventEntity;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.secure.entity.MessageEntity;
import io.github.gms.secure.entity.SecretEntity;
import io.github.gms.secure.entity.SystemPropertyEntity;
import io.github.gms.secure.entity.UserEntity;
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

	public static ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		
		return mapper;
	}

	public static HttpHeaders getApiHttpHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(ApiController.API_KEY_HEADER, apiKey);

		return headers;
	}
	
	public static HttpHeaders getHttpHeaders(String jwt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);	

		if (jwt != null) {
			headers.add("Cookie", Constants.ACCESS_JWT_TOKEN + "=" + jwt + ";Max-Age=3600;HttpOnly");
		}

		return headers;
	}
	
	public static Cookie getCookie(String jwt) {
		Cookie cookie = new Cookie(Constants.ACCESS_JWT_TOKEN, jwt);
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
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_1_ID)
				.username(DemoData.USERNAME1)
				.authorities(Sets.newHashSet(UserRole.ROLE_USER))
				.build();
	}
	
	public static GmsUserDetails createBlockedGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.enabled(false)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_1_ID)
				.username(DemoData.USERNAME1)
				.authorities(Sets.newHashSet(UserRole.ROLE_USER))
				.build();
	}
	
	public static GmsUserDetails createLockedGmsUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(false)
				.enabled(true)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_1_ID)
				.username(DemoData.USERNAME1)
				.authorities(Sets.newHashSet(UserRole.ROLE_USER))
				.build();
	}
	
	public static GmsUserDetails createGmsAdminUser() {
		return GmsUserDetails.builder()
				.accountNonLocked(true)
				.credential(DemoData.CREDENTIAL_TEST)
				.userId(DemoData.USER_2_ID)
				.username(DemoData.USERNAME2)
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
		request.setName("api key 1");
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
	
	public static ApiKeyEntity createApiKey(Long id, String value) {
		ApiKeyEntity user = new ApiKeyEntity();
		user.setId(id);
		user.setUserId(1L);
		user.setStatus(EntityStatus.ACTIVE);
		user.setValue(value);
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
		entity.setEventDate(ZonedDateTime.now().minusHours(2));
		entity.setOperation(EventOperation.GET_BY_ID);
		entity.setTarget(EventTarget.KEYSTORE);
		entity.setUserId("user");
		return entity;
	}

	public static SecretEntity createSecretEntity(Long id, Long keystoreAliasId, String secretId) {
		SecretEntity entity = new SecretEntity();
		entity.setId(id);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue(DemoData.ENCRYPTED_VALUE);
		entity.setSecretId(secretId);;
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
		entity.setCreationDate(ZonedDateTime.now());
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue("test");
		entity.setKeystoreAliasId(keystoreAliasId);
		entity.setLastRotated(ZonedDateTime.now());
		entity.setUserId(1L);
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
	
	public static KeystoreEntity createKeystoreEntity(Long id, String credential) {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setName("keystore");
		entity.setId(id);
		entity.setFileName("test.jks");
		entity.setUserId(DemoData.USER_1_ID);
		entity.setCredential(credential);
		entity.setType(KeystoreType.JKS);
		entity.setDescription("description");
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setCreationDate(ZonedDateTime.now());
		return entity;
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
	
	public static KeystoreAliasEntity createKeystoreAliasEntity(Long id) {
		KeystoreAliasEntity entity = new KeystoreAliasEntity();
		
		entity.setId(id);
		entity.setKeystoreId(DemoData.KEYSTORE_ID);
		entity.setAlias("test");
		entity.setAliasCredential("test");
		entity.setDescription("description");
		
		return entity;
	}

	public static KeystoreAliasEntity createKeystoreAliasEntity() {
		return createKeystoreAliasEntity(DemoData.KEYSTORE_ALIAS_ID);
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
		dto.setRoles(Sets.newHashSet(UserRole.ROLE_USER));
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
		user.setRoles(Sets.newHashSet(UserRole.ROLE_USER));
		user.setStatus(EntityStatus.ACTIVE);
		return user;
	}
	
	public static SaveSecretRequestDto createSaveSecretRequestDto(Long secretId, Set<Long> apiKeyRestrictionList) {
		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setId(secretId);
		dto.setRotationPeriod(RotationPeriod.YEARLY);
		dto.setStatus(EntityStatus.ACTIVE);
		dto.setType(SecretType.CREDENTIAL);;
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
		entity1.setAnnouncementDate(ZonedDateTime.now().minusDays(1));
		return new PageImpl<AnnouncementEntity>(Lists.newArrayList(entity1));
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
		return createJwtAdminRequest(GmsUserDetails.builder()
				.userId(DemoData.USER_1_ID)
				.username(DemoData.USERNAME1)
				.authorities(Set.of(UserRole.ROLE_ADMIN))
				.build());
	}

	public static GenerateJwtRequest createJwtAdminRequest(GmsUserDetails user) {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), user.getUserId(),
				MdcParameter.USER_NAME.getDisplayName(), user.getUsername(),
				"roles", user.getAuthorities().stream() .map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
		);

		return GenerateJwtRequest.builder().subject(user.getUsername()).algorithm("HS512")
				.expirationDateInSeconds(30L)
				.claims(claims)
				.build();
	}
	
	public static GenerateJwtRequest createJwtUserRequest() {
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), DemoData.USER_1_ID,
				MdcParameter.USER_NAME.getDisplayName(), DemoData.USERNAME1,
				"roles", List.of("ROLE_USER")
		);

		return GenerateJwtRequest.builder().subject(DemoData.USERNAME1).algorithm("HS512")
				.expirationDateInSeconds(30L)
				.claims(claims)
				.build();
	}
}
