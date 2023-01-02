package io.github.gms.auth.db;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.gms.auth.UserAuthService;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.entity.UserEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.repository.UserRepository;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@Profile(Constants.CONFIG_AUTH_TYPE_DB)
public class DbUserAuthServiceImpl implements UserAuthService {
	
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> entityOptional = repository.findByUsername(username);

		if (entityOptional.isEmpty()) {
			throw new UsernameNotFoundException("User not found!");
		}
		
		UserEntity user = entityOptional.get();
		Set<UserRole> authorities = Stream.of(user.getRoles().split(";")).map(UserRole::valueOf).collect(Collectors.toSet());
		return GmsUserDetails.builder()
				.userId(user.getId())
				.username(user.getUsername())
				.name(user.getName())
				.credential(user.getCredential())
				.email(user.getEmail())
				.authorities(authorities)
				.accountNonLocked(user.getStatus() == EntityStatus.ACTIVE)
				.enabled(user.getStatus() == EntityStatus.ACTIVE)
				.build();
	}
}
