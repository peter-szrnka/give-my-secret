package io.github.gms.functions.user;

import com.google.common.collect.Sets;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.LDAP_CRYPT_PREFIX;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class UserConverterImpl implements UserConverter {

	private final Clock clock;
	private final PasswordEncoder passwordEncoder;
	@Setter
	@Value("${config.store.ldap.credential:false}")
	private boolean storeLdapCredential;

	@Override
	public UserEntity toNewEntity(SaveUserRequestDto dto, boolean roleChangeEnabled) {
		UserEntity entity = new UserEntity();

		entity.setUsername(dto.getUsername());
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setCredential(passwordEncoder.encode(dto.getCredential()));
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setStatus(EntityStatus.ACTIVE);
		
		if (roleChangeEnabled) {
			entity.setRoles(dto.getRoles().stream().map(Enum::name).collect(Collectors.joining(";")));
		}

		return entity;
	}

	@Override
	public UserEntity toEntity(UserEntity entity, SaveUserRequestDto dto, boolean roleChangeEnabled) {
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setUsername(dto.getUsername());
		entity.setEmail(dto.getEmail());
		
		if (dto.getCredential() != null) {
			entity.setCredential(passwordEncoder.encode(dto.getCredential()));
		}
		entity.setStatus(dto.getStatus());
		
		if (roleChangeEnabled) {
			entity.setRoles(dto.getRoles().stream().map(Enum::name).collect(Collectors.joining(";")));
		}

		return entity;
	}

	@Override
	public UserDto toDto(UserEntity entity) {
		UserDto dto = new UserDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setUsername(entity.getUsername());
		dto.setEmail(entity.getEmail());
		dto.setStatus(entity.getStatus());
		dto.setRoles(Sets.newHashSet(Stream.of(entity.getRoles().split(";")).map(UserRole::getByName).collect(Collectors.toSet())));
		dto.setCreationDate(entity.getCreationDate());

		return dto;
	}

	@Override
	public UserListDto toDtoList(Page<UserEntity> resultList) {
		List<UserDto> results = resultList.toList().stream().map(this::toDto).toList();
		return UserListDto.builder().resultList(results).totalElements(resultList.getTotalElements()).build();
	}

	@Override
	public UserInfoDto toUserInfoDto(GmsUserDetails user, boolean mfaRequired) {
		UserInfoDto dto = new UserInfoDto();
		dto.setUsername(user.getUsername());

		if (mfaRequired) {
			return dto;
		}

		dto.setId(user.getUserId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setRoles(Sets.newHashSet(user.getAuthorities().stream().map(authority -> UserRole.getByName(authority.getAuthority())).collect(Collectors.toSet())));

		return dto;
	}

	@Override
	public UserDetails addIdToUserDetails(GmsUserDetails foundUser, Long id) {
		foundUser.setUserId(id);
		return foundUser;
	}

	@Override
	public UserEntity toEntity(GmsUserDetails foundUser, UserEntity existingEntity) {
		UserEntity entity = existingEntity == null ? new UserEntity() : existingEntity;

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
		return entity;
	}

	private String getCredential(GmsUserDetails foundUser) {
		return storeLdapCredential ? foundUser.getCredential().replace(LDAP_CRYPT_PREFIX, "")
				: "*PROVIDED_BY_LDAP*";
	}
}
