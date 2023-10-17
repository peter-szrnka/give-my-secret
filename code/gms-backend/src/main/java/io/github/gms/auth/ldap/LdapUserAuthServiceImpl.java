package io.github.gms.auth.ldap;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.GmsUserDetails;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@Profile(value = { CONFIG_AUTH_TYPE_LDAP })
public class LdapUserAuthServiceImpl implements UserAuthService {

	private final LdapTemplate ldapTemplate;
	private final LdapUserPersistenceService ldapUserPersistenceService;

	public LdapUserAuthServiceImpl(LdapTemplate ldapTemplate, LdapUserPersistenceService ldapUserPersistenceService) {
		this.ldapTemplate = ldapTemplate;
		this.ldapUserPersistenceService = ldapUserPersistenceService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<GmsUserDetails> result = ldapTemplate.search(LdapQueryBuilder.query().where("uid").is(username),
				new LDAPAttributesMapper());

		if (result.size() != 1) {
			throw new UsernameNotFoundException("User not found!");
		}

		return ldapUserPersistenceService.saveUserIfRequired(username, result.get(0));
	}
}