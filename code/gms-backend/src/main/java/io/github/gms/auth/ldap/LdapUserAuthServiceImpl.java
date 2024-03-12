package io.github.gms.auth.ldap;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_LDAP })
public class LdapUserAuthServiceImpl implements UserAuthService {

	private final LdapTemplate ldapTemplate;
	private final UserRepository userRepository;
	private final UserConverter userConverter;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<GmsUserDetails> result = ldapTemplate.search(LdapQueryBuilder.query().where("uid").is(username),
				new LDAPAttributesMapper());

		if (result.size() != 1) {
			throw new UsernameNotFoundException("User not found!");
		}

		return userConverter.addIdToUserDetails(result.getFirst(),
				userRepository.getIdByUsername(username).orElseThrow(() -> new GmsException("User not found!")));
	}
}