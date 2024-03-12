package io.github.gms.auth.ldap;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;
import static io.github.gms.common.util.Constants.LDAP_CRYPT_PREFIX;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@Profile(value = { CONFIG_AUTH_TYPE_LDAP })
public class LdapUserPersistenceServiceImpl implements LdapUserPersistenceService {

    private final Clock clock;
	private final LdapTemplate ldapTemplate;
    private final UserRepository repository;
    private final boolean storeLdapCredential;

    public LdapUserPersistenceServiceImpl(
			Clock clock,
			LdapTemplate ldapTemplate,
			UserRepository repository,
			@Value("${config.store.ldap.credential:false}") boolean storeLdapCredential
	) {
        this.clock = clock;
		this.ldapTemplate = ldapTemplate;
        this.repository = repository;
        this.storeLdapCredential = storeLdapCredential;
    }

	@Override
	public void synchronizeUsers() {
		List<GmsUserDetails> result = ldapTemplate.search(LdapQueryBuilder.query(),
				new LDAPAttributesMapper());

		result.forEach(this::saveOrUpdateUser);
	}

    private void saveOrUpdateUser(GmsUserDetails foundUser) {
        repository.findByUsername(foundUser.getUsername()).ifPresentOrElse(userEntity -> saveUser(foundUser, userEntity), () ->
				saveUser(foundUser, null));
    }

	private void saveUser(GmsUserDetails foundUser, UserEntity userEntity) {
		UserEntity entity = userEntity == null ? new UserEntity() : userEntity;

		entity.setStatus(EntityStatus.ACTIVE);
		entity.setName(foundUser.getName());
		entity.setUsername(foundUser.getUsername());
		entity.setCredential(getCredential(foundUser));
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setEmail(foundUser.getEmail());
		entity.setRoles(foundUser.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(",")));
		entity.setMfaEnabled(foundUser.isMfaEnabled());
		SecretGenerator secretGenerator = new DefaultSecretGenerator();
		entity.setMfaSecret(secretGenerator.generate());
		entity = repository.save(entity);

		foundUser.setUserId(entity.getId());
		log.info("User data has been saved into DB for user={}", foundUser.getUsername());
	}

    private String getCredential(GmsUserDetails foundUser) {
		return storeLdapCredential ? foundUser.getCredential().replace(LDAP_CRYPT_PREFIX, "")
				: "*PROVIDED_BY_LDAP*";
	}
}