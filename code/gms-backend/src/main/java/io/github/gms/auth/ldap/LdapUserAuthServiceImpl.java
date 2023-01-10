package io.github.gms.auth.ldap;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@Profile(value = { Constants.CONFIG_AUTH_TYPE_LDAP, Constants.CONFIG_AUTH_TYPE_LDAP_TEST })
public class LdapUserAuthServiceImpl implements UserAuthService {

	@Value("${config.store.ldap.credential:false}")
	private boolean storeLdapCredential;
	@Autowired
	private Clock clock;
	@Autowired
	private UserRepository repository;
	@Autowired
	private LdapTemplate ldapTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<GmsUserDetails> result = ldapTemplate.search(LdapQueryBuilder.query().where("uid").is(username),
				new LDAPAttributesMapper());

		if (result.isEmpty() || result.size() > 1) {
			throw new UsernameNotFoundException("User not found!");
		}

		GmsUserDetails foundUser = result.get(0);

		repository.findByUsername(username).ifPresentOrElse(userEntity -> saveExistingUser(foundUser, userEntity),
				() -> saveNewUser(foundUser));
		return foundUser;
	}

	private void saveExistingUser(GmsUserDetails foundUser, UserEntity userEntity) {
		foundUser.setUserId(userEntity.getId());

		if (storeLdapCredential && !userEntity.getCredential().equals(foundUser.getCredential())) {
			userEntity.setCredential(getCredential(foundUser));
			repository.save(userEntity);
			log.info("Credential has been updated for user={}", foundUser.getUsername());
		}
	}

	private void saveNewUser(GmsUserDetails foundUser) {
		UserEntity userEntity = new UserEntity();
		userEntity.setStatus(EntityStatus.ACTIVE);
		userEntity.setName(foundUser.getName());
		userEntity.setUsername(foundUser.getUsername());
		userEntity.setCredential(getCredential(foundUser));
		userEntity.setCreationDate(LocalDateTime.now(clock));
		userEntity.setEmail(foundUser.getEmail());
		userEntity.setRoles(foundUser.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(",")));
		userEntity = repository.save(userEntity);

		foundUser.setUserId(userEntity.getId());
		log.info("User data has been saved into DB for user={}", foundUser.getUsername());
	}

	private String getCredential(GmsUserDetails foundUser) {
		return storeLdapCredential ? foundUser.getCredential().replace(Constants.LDAP_CRYPT_PREFIX, "")
				: "*PROVIDED_BY_LDAP*";
	}
}
