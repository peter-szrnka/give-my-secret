package io.github.gms.util;

import io.github.gms.common.enums.*;
import io.github.gms.functions.announcement.AnnouncementEntity;
import io.github.gms.functions.announcement.AnnouncementRepository;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionEntity;
import io.github.gms.functions.iprestriction.IpRestrictionRepository;
import io.github.gms.functions.keystore.KeystoreAliasEntity;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.keystore.KeystoreEntity;
import io.github.gms.functions.keystore.KeystoreRepository;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.maintenance.job.JobRepository;
import io.github.gms.functions.message.MessageEntity;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static io.github.gms.util.DemoData.*;
import static io.github.gms.util.TestConstants.TEST;
import static io.github.gms.util.TestUtils.createMfaUser;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
public class DemoDataManagerService {

	private static final String DESCRIPTION = "description";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ApiKeyRepository apiKeyRepository;
	@Autowired
	private KeystoreRepository keystoreRepository;
	@Autowired
	private KeystoreAliasRepository keystoreAliasRepository;
	@Autowired
	private SecretRepository secretRepository;
	@Autowired
	private AnnouncementRepository announcementRepository;
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private IpRestrictionRepository ipRestrictionRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SystemPropertyService systemPropertyService;
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private SystemAttributeRepository systemAttributeRepository;

	public void initTestData() {
		// User
		userRepository.save(createUser(USER_1_ID, USERNAME1, "ROLE_USER"));
		userRepository.save(createUser(USER_2_ID, USERNAME2, "ROLE_ADMIN"));
		userRepository.save(createMfaUser());
		
		// Api key
		apiKeyRepository.save(createApiKey(USER_1_ID, API_KEY_1_ID, API_KEY_CREDENTIAL1));
		apiKeyRepository.save(createApiKey(USER_2_ID, API_KEY_2_ID, API_KEY_CREDENTIAL2));
		
		// Keystore
		keystoreRepository.save(createKeystore(KEYSTORE_ID));
		keystoreRepository.save(createKeystore(KEYSTORE2_ID));
		
		// Keystore alias
		keystoreAliasRepository.save(createKeystoreAlias(KEYSTORE_ID, KEYSTORE_ALIAS_ID));
		keystoreAliasRepository.save(createKeystoreAlias(KEYSTORE_ID, KEYSTORE_ALIAS2_ID));
		
		// Secret
		secretRepository.save(createSecret(SECRET_ENTITY_ID, SECRET_ID1, false, KEYSTORE_ALIAS_ID));
		secretRepository.save(createSecret(SECRET_ENTITY2_ID, SECRET_ID2, true, KEYSTORE_ALIAS2_ID));
		
		// Announcement
		announcementRepository.save(createAnnouncement(ANNOUNCEMENT_ID));
		
		// Message
		messageRepository.save(createMessage());

		// IP Restrictions
		ipRestrictionRepository.save(createGlobalIpRestriction());

		// System properties
		disableJob(SystemProperty.EVENT_MAINTENANCE_JOB_ENABLED);
		disableJob(SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED);
		disableJob(SystemProperty.LDAP_SYNC_JOB_ENABLED);
		disableJob(SystemProperty.MESSAGE_CLEANUP_JOB_ENABLED);
		disableJob(SystemProperty.SECRET_ROTATION_JOB_ENABLED);
		disableJob(SystemProperty.USER_DELETION_JOB_ENABLED);

		// Job logs
		JobEntity jobEntity = TestUtils.createJobEntity();
		jobEntity.setId(null);
		jobRepository.save(jobEntity);

		// System status
		systemAttributeRepository.save(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP));

		// End
		log.info("Test data's have been configured!");
	}

	private void disableJob(SystemProperty systemProperty) {
		systemPropertyService.save(SystemPropertyDto.builder()
				.key(systemProperty.name())
				.value("false")
				.type(PropertyType.BOOLEAN)
				.category(SystemPropertyCategory.JOB)
				.lastModified(ZonedDateTime.now())
				.build());
	}

	private IpRestrictionEntity createGlobalIpRestriction() {
		return IpRestrictionEntity.builder()
				.id(1L)
				.global(true)
				.allow(true)
				.ipPattern("(127.0.0.)[0-9]{1,3}")
				.creationDate(ZonedDateTime.now().minusDays(1L))
				.lastModified(ZonedDateTime.now())
				.status(EntityStatus.ACTIVE)
				.build();
	}

	public static ApiKeyEntity createApiKey(Long userId, Long apiKeyId, String value) {
		ApiKeyEntity entity = new ApiKeyEntity();
		entity.setId(apiKeyId);
		entity.setUserId(userId);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue(value);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setDescription(DESCRIPTION);
		entity.setName(TEST);
		return entity;
	}

	private UserEntity createUser(Long id, String userId, String role) {
		UserEntity entity = new UserEntity();
		entity.setId(id);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setUsername(userId);
		entity.setCredential(passwordEncoder.encode(CREDENTIAL_TEST));
		entity.setRole(UserRole.getByName(role));
		entity.setName(userId);
		entity.setEmail("a@b.hu");
		
		return entity;
	}

	private static KeystoreEntity createKeystore(Long id) {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setDescription(DESCRIPTION);
		entity.setName(TEST);
		entity.setCredential(CREDENTIAL_TEST);
		entity.setId(id);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setType(KeystoreType.JKS);
		entity.setUserId(USER_1_ID);
		entity.setFileName("test.jks");
		return entity;
	}
	
	private static KeystoreAliasEntity createKeystoreAlias(Long keystoreId, Long id) {
		KeystoreAliasEntity entity = new KeystoreAliasEntity();

		entity.setId(id);
		entity.setKeystoreId(keystoreId);
		entity.setAlias(TEST);
		entity.setAliasCredential(CREDENTIAL_TEST);
		entity.setDescription(DESCRIPTION);
		
		return entity;
	}
	
	private static SecretEntity createSecret(Long id, String secretId, boolean returnDecrypted, Long keystoreAliasId) {
		SecretEntity entity = new SecretEntity();
		entity.setId(id);
		entity.setCreationDate(ZonedDateTime.now().minusDays(2));
		entity.setKeystoreAliasId(keystoreAliasId);
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setUserId(USER_1_ID);
		entity.setValue(ENCRYPTED_VALUE);
		entity.setType(SecretType.SIMPLE_CREDENTIAL);
		entity.setSecretId(secretId);
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setReturnDecrypted(returnDecrypted);
		entity.setLastRotated(ZonedDateTime.now().minusDays(1));
		entity.setLastUpdated(ZonedDateTime.now().minusDays(1));
		entity.setRotationEnabled(true);
		return entity;
	}
	

	private static AnnouncementEntity createAnnouncement(Long announcementId) {
		AnnouncementEntity entity = new AnnouncementEntity();
		entity.setId(announcementId);
		entity.setTitle("title");
		entity.setDescription(DESCRIPTION);
		entity.setAuthorId(USER_1_ID);
		entity.setAnnouncementDate(ZonedDateTime.now());
		return entity;
	}
	
	private static MessageEntity createMessage() {
		return MessageEntity.builder().id(1L).message("test message").opened(false).userId(1L).build();
	}
}