package io.github.gms.auth.db;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_DB;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_DB)
public class DbUserAuthServiceImpl implements UserAuthService {

	private final UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

		Set<UserRole> authorities = Set.of(user.getRole());
		return GmsUserDetails.builder()
				.userId(user.getId())
				.username(user.getUsername())
				.name(user.getName())
				.credential(user.getCredential())
				.email(user.getEmail())
				.authorities(authorities)
				.accountNonLocked(user.getStatus() == EntityStatus.ACTIVE)
				.enabled(user.getStatus() == EntityStatus.ACTIVE)
				.mfaEnabled(user.isMfaEnabled())
				.mfaSecret(user.getMfaSecret())
				.build();
	}
}
