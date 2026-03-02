package io.github.gms.util;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.*;
import io.github.gms.functions.announcement.AnnouncementEntity;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.iprestriction.IpRestrictionEntity;
import io.github.gms.functions.keystore.KeystoreAliasEntity;
import io.github.gms.functions.keystore.KeystoreEntity;
import io.github.gms.functions.maintenance.job.JobEntity;
import io.github.gms.functions.message.MessageEntity;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Map;

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
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SystemPropertyService systemPropertyService;
	@Autowired
	private SystemAttributeRepository systemAttributeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Map<String, Long> entityMap;

    private  <T extends AbstractGmsEntity> void saveAndPutEntity(String domain, Long code, T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityMap.put(domain + "-" + code, entity.getId());
    }

    private Long getEntityId(String domain, Long code) {
        return entityMap.get(domain + "-" + code);
    }

    @Transactional
	public void initTestData() {
		// User
        saveAndPutEntity("user", USER_1_ID, createUser(USERNAME1, "ROLE_USER"));
        saveAndPutEntity("user", USER_2_ID, createUser(USERNAME2, "ROLE_ADMIN"));
        saveAndPutEntity("user", MFA_USER_ID, createMfaUser());

        Long user1Id = getEntityId("user", USER_1_ID);
        Long user2Id = getEntityId("user", USER_2_ID);
		
		// Api key
        saveAndPutEntity("apiKey", API_KEY_1_ID, createApiKey(user1Id, API_KEY_CREDENTIAL1));
        saveAndPutEntity("apiKey", API_KEY_2_ID, createApiKey(user2Id, API_KEY_CREDENTIAL2));
		
		// Keystore
        saveAndPutEntity("keystore", KEYSTORE_ID, createKeystore(user1Id));
        saveAndPutEntity("keystore", KEYSTORE2_ID, createKeystore(user1Id));

        Long keystore1Id = getEntityId("keystore", KEYSTORE_ID);
		
		// Keystore alias
        saveAndPutEntity("keystoreAlias", KEYSTORE_ALIAS_ID, createKeystoreAlias(keystore1Id));
        saveAndPutEntity("keystoreAlias", KEYSTORE_ALIAS2_ID, createKeystoreAlias(keystore1Id));

        Long keystoreAlias1Id = getEntityId("keystoreAlias", KEYSTORE_ALIAS_ID);
        Long keystoreAlias2Id = getEntityId("keystoreAlias", KEYSTORE_ALIAS2_ID);
		
		// Secret
        saveAndPutEntity("secret", SECRET_ENTITY_ID, createSecret(user1Id, SECRET_ID1, false, keystoreAlias1Id));
        saveAndPutEntity("secret", SECRET_ENTITY2_ID, createSecret(user1Id, SECRET_ID2, true, keystoreAlias2Id));

		// Announcement
        saveAndPutEntity("announcement", ANNOUNCEMENT_ID, createAnnouncement(user1Id));
		
		// Message
        saveAndPutEntity("message", 1L, createMessage(user1Id));

		// IP Restrictions
        saveAndPutEntity("ipRestriction", 1L, createGlobalIpRestriction());

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
		saveAndPutEntity("job", 1L, jobEntity);

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

		log.info("Job {} has been disabled!", systemProperty.name());
	}

	private IpRestrictionEntity createGlobalIpRestriction() {
		return IpRestrictionEntity.builder()
				.global(true)
				.allow(true)
				.ipPattern("(127.0.0.)[0-9]{1,3}")
				.creationDate(ZonedDateTime.now().minusDays(1L))
				.lastModified(ZonedDateTime.now())
				.status(EntityStatus.ACTIVE)
				.build();
	}

	public static ApiKeyEntity createApiKey(Long userId, String value) {
		ApiKeyEntity entity = new ApiKeyEntity();
		entity.setUserId(userId);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setValue(value);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setDescription(DESCRIPTION);
		entity.setName(TEST);
		return entity;
	}

    private UserEntity createUser(String userId, String role) {
        UserEntity entity = new UserEntity();
        entity.setCreationDate(ZonedDateTime.now());
        entity.setStatus(EntityStatus.ACTIVE);
        entity.setUsername(userId);
        entity.setCredential(passwordEncoder.encode(CREDENTIAL_TEST));
        entity.setRole(UserRole.getByName(role));
        entity.setName(userId);
        entity.setEmail("a@b.hu");

        return entity;
    }

	private static KeystoreEntity createKeystore(Long userId) {
		KeystoreEntity entity = new KeystoreEntity();
		entity.setDescription(DESCRIPTION);
		entity.setName(TEST);
		entity.setCredential(CREDENTIAL_TEST);
		entity.setCreationDate(ZonedDateTime.now());
		entity.setStatus(EntityStatus.ACTIVE);
		entity.setType(KeystoreType.JKS);
		entity.setUserId(userId);
		entity.setFileName("test.jks");
		return entity;
	}
	
	private static KeystoreAliasEntity createKeystoreAlias(Long keystoreId) {
		KeystoreAliasEntity entity = new KeystoreAliasEntity();

		entity.setKeystoreId(keystoreId);
		entity.setAlias(TEST);
		entity.setAliasCredential(CREDENTIAL_TEST);
		entity.setDescription(DESCRIPTION);
		
		return entity;
	}
	
	private static SecretEntity createSecret(Long userId, String secretId, boolean returnDecrypted, Long keystoreAliasId) {
		SecretEntity entity = new SecretEntity();
		entity.setCreationDate(ZonedDateTime.now().minusDays(2));
		entity.setKeystoreAliasId(keystoreAliasId);
		entity.setRotationPeriod(RotationPeriod.YEARLY);
		entity.setUserId(userId);
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
	

	private static AnnouncementEntity createAnnouncement(Long authorId) {
		AnnouncementEntity entity = new AnnouncementEntity();
		entity.setTitle("title");
		entity.setDescription(DESCRIPTION);
		entity.setAuthorId(authorId);
		entity.setAnnouncementDate(ZonedDateTime.now());
		return entity;
	}
	
	private static MessageEntity createMessage(Long userId) {
		return MessageEntity.builder().message("test message").opened(false).userId(userId).build();
	}
}