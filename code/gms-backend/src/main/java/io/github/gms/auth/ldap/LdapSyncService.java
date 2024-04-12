package io.github.gms.auth.ldap;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_USER;
import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@Profile(value = { CONFIG_AUTH_TYPE_LDAP })
public class LdapSyncService {

	private final LdapTemplate ldapTemplate;
    private final UserRepository repository;
	private final LdapUserConverter converter;
	private final String authType;

    public LdapSyncService(
			LdapTemplate ldapTemplate,
			UserRepository repository,
			LdapUserConverter converter,
			@Value("${config.auth.type}") String authType
	) {
		this.ldapTemplate = ldapTemplate;
        this.repository = repository;
		this.converter = converter;
		this.authType = authType;
    }

	@CacheEvict(cacheNames = { CACHE_USER, CACHE_API }, allEntries = true)
	public Pair<Integer, Integer> synchronizeUsers() {
		if (!SELECTED_AUTH_LDAP.equals(authType)) {
			return Pair.of(0, 0);
		}

		List<GmsUserDetails> result = ldapTemplate.search(LdapQueryBuilder.query().where("uid").like("*"),
				new LDAPAttributesMapper());

		AtomicInteger counter = new AtomicInteger(0);
		result.forEach(item -> saveOrUpdateUser(item, counter));

		AtomicInteger toBeDeletedCounter = new AtomicInteger(0);
		List<String> usernamesFromLdap = result.stream().map(GmsUserDetails::getUsername).toList();
		repository.getAllUserNames().stream()
				.filter(username -> !usernamesFromLdap.contains(username))
				.forEach(username -> blockUser(username, toBeDeletedCounter));

		return Pair.of(counter.get(), toBeDeletedCounter.get());
	}

	private void blockUser(String username, AtomicInteger deletedCounter) {
		repository.markUserAsDeleted(username);
		deletedCounter.incrementAndGet();
	}

    private void saveOrUpdateUser(GmsUserDetails foundUser, AtomicInteger counter) {
        repository.findByUsername(foundUser.getUsername()).ifPresentOrElse(existingEntity -> saveUser(foundUser, existingEntity), () ->
				saveUser(foundUser, null));
		counter.incrementAndGet();
    }

	private void saveUser(GmsUserDetails foundUser, UserEntity existingEntity) {
		UserEntity entity = converter.toEntity(foundUser, existingEntity);
		entity = repository.save(entity);

		foundUser.setUserId(entity.getId());
		log.info("User data has been saved into DB for user={}", foundUser.getUsername());
	}
}