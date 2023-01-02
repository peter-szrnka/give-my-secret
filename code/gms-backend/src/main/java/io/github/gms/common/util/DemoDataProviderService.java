package io.github.gms.common.util;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.gms.common.entity.AnnouncementEntity;
import io.github.gms.common.entity.ApiKeyEntity;
import io.github.gms.common.entity.KeystoreEntity;
import io.github.gms.common.entity.MessageEntity;
import io.github.gms.common.entity.SecretEntity;
import io.github.gms.common.entity.UserEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeystoreType;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.secure.repository.AnnouncementRepository;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.MessageRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
public class DemoDataProviderService {
	
	public static final String SECRET_VALUE = "test";
	public static final String SECRET_ID1 = "SECRET_ID";
	public static final String SECRET_ID2 = "SECRET_ID2";
	public static final String CREDENTIAL_TEST = "test";
	public static final Long USER_1_ID = 1L;
	public static final Long USER_2_ID = 2L;
	public static final Long API_KEY_1_ID = 1L;
	public static final Long API_KEY_2_ID = 2L;
	public static final Long KEYSTORE_ID = 1L;
	public static final Long KEYSTORE2_ID = 2L;
	public static final Long SECRET_ENTITY_ID = 1L;
	public static final Long SECRET_ENTITY2_ID = 2L;
	public static final Long ANNOUNCEMENT_ID = 1L;
	
	public static final String API_KEY_CREDENTIAL1 = "12345678";
	public static final String API_KEY_CREDENTIAL2 = "23456789";
	public static final String USERNAME1 = "username1";
	public static final String USERNAME2 = "username2";
	
	public static final String ENCRYPTED_VALUE =
		"I+sC4r7asrdGPuPi+mR3O/hJRZ47gVMTigE40tPfkAbo2hfl7V+KxgvoNg7dUv1Bv/JVLVE1GefmHCIk4KteQpilPdNo6lQnE2YldU0+eldGMUNSZnnjW5Qm946dGyHjb3dd9ZY4xXUfKKdgisJUde5CZySDDbarQAg7FbkQkXJc5rtJ8iJOh8R5QX3OnA8J6YuepmZ6kShYDHZi13O3exCEr+PL9r6ctKKDvH/LG1IldAtMTc7dBAGqxD/WCPdZjGXySEBR5M2eOQlcT6VKvhJM1598hbsx5iZ6IIzfdk9IlvhSFabBLkYF3n/7PKlm0buR/9avvcJxvRprCOV1MA";
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ApiKeyRepository apiKeyRepository;
	@Autowired
	private KeystoreRepository keystoreRepository;
	@Autowired
	private SecretRepository secretRepository;
	@Autowired
	private AnnouncementRepository announcementRepository;
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void initTestData() {
		// User
		userRepository.save(createUser(USER_1_ID, USERNAME1, "ROLE_USER"));
		userRepository.save(createUser(USER_2_ID, USERNAME2, "ROLE_ADMIN"));
		
		// Api key
		apiKeyRepository.save(createApiKey(USER_1_ID, API_KEY_1_ID, API_KEY_CREDENTIAL1));
		apiKeyRepository.save(createApiKey(USER_2_ID, API_KEY_2_ID, API_KEY_CREDENTIAL2));
		
		// Keystore
		keystoreRepository.save(createKeystore(KEYSTORE_ID));
		keystoreRepository.save(createKeystore(KEYSTORE2_ID));
		
		// Secret
		secretRepository.save(createSecret(SECRET_ENTITY_ID, SECRET_ID1, false, KEYSTORE_ID));
		secretRepository.save(createSecret(SECRET_ENTITY2_ID, SECRET_ID2, true, KEYSTORE2_ID));
		
		// Announcement
		announcementRepository.save(createAnnouncement(ANNOUNCEMENT_ID));
		
		// Message
		messageRepository.save(createMessage());
		
		// End
		log.info("Test data's have been configured!");
	}

	public static ApiKeyEntity createApiKey(Long userId, Long apiKeyId, String value) {
		ApiKeyEntity entity = new ApiKeyEntity();
		entity.setId(apiKeyId);
		entity.setUserId(userId);
		entity.setCreationDate(LocalDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue(value);
		entity.setCreationDate(LocalDateTime.now());
		entity.setDescription("description");
		entity.setName("test");
		return entity;
	}

	private UserEntity createUser(Long id, String userId, String role) {
		UserEntity entity = new UserEntity();
		entity.setId(id);
		entity.setCreationDate(LocalDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setUsername(userId);
		entity.setCredential(passwordEncoder.encode(CREDENTIAL_TEST));
		entity.setRoles(role);
		entity.setName(userId);
		entity.setEmail("a@b.hu");
		
		return entity;
	}

	private static KeystoreEntity createKeystore(Long id) {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setDescription("description");
		entity.setName("test");
		entity.setAlias("test");
		entity.setAliasCredential(CREDENTIAL_TEST);
		entity.setCredential(CREDENTIAL_TEST);
		entity.setId(id);
		entity.setCreationDate(LocalDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setType(KeystoreType.JKS);
		entity.setUserId(USER_1_ID);
		entity.setFileName("test.jks");
		return entity;
	}
	
	private static SecretEntity createSecret(Long id, String secretId, boolean returnDecrypted, Long keystoreId) {
		SecretEntity entity = new SecretEntity();
		entity.setId(id);
		entity.setCreationDate(LocalDateTime.now().minusDays(2));
		entity.setKeystoreId(keystoreId);
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setUserId(USER_1_ID);
		entity.setValue(ENCRYPTED_VALUE);
		entity.setSecretId(secretId);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setReturnDecrypted(returnDecrypted);
		entity.setLastRotated(LocalDateTime.now().minusDays(1));
		entity.setLastUpdated(LocalDateTime.now().minusDays(1));
		entity.setRotationEnabled(true);
		return entity;
	}
	

	private static AnnouncementEntity createAnnouncement(Long announcementId) {
		AnnouncementEntity entity = new AnnouncementEntity();
		entity.setId(announcementId);
		entity.setTitle("title");
		entity.setDescription("description");
		entity.setAuthorId(USER_1_ID);
		entity.setAnnouncementDate(LocalDateTime.now());
		return entity;
	}
	
	private static MessageEntity createMessage() {
		return MessageEntity.builder().id(1L).message("test message").opened(false).userId(1L).build();
	}
}