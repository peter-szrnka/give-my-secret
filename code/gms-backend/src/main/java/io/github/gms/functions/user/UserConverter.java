package io.github.gms.functions.user;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.abstraction.AbstractUserConverter;
import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class UserConverter extends AbstractUserConverter implements GmsConverter<UserListDto, UserEntity> {

	private final Clock clock;
	private final PasswordEncoder passwordEncoder;

	public UserEntity toNewEntity(SaveUserRequestDto dto, boolean roleChangeEnabled) {
		UserEntity entity = new UserEntity();

		entity.setUsername(dto.getUsername());
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setCredential(passwordEncoder.encode(dto.getCredential()));
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setStatus(EntityStatus.ACTIVE);

		if (roleChangeEnabled) {
			entity.setRole(dto.getRole());
		}

		return entity;
	}

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
			entity.setRole(dto.getRole());
		}

		return entity;
	}

	public UserDto toDto(UserEntity entity) {
		UserDto dto = new UserDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setUsername(entity.getUsername());
		dto.setEmail(entity.getEmail());
		dto.setStatus(entity.getStatus());
		dto.setRole(entity.getRole());
		dto.setCreationDate(entity.getCreationDate());

		return dto;
	}

	@Override
	public UserListDto toDtoList(Page<UserEntity> resultList) {
		List<UserDto> results = resultList.toList().stream().map(this::toDto).toList();
		return UserListDto.builder().resultList(results).totalElements(resultList.getTotalElements()).build();
	}

	public UserInfoDto toUserInfoDto(GmsUserDetails user, boolean mfaRequired) {
		UserInfoDto dto = new UserInfoDto();
		dto.setUsername(user.getUsername());

		if (mfaRequired) {
			return dto;
		}

		dto.setId(user.getUserId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setRole(getFirstRole(user.getAuthorities()));

		return dto;
	}

	public GmsUserDetails addIdToUserDetails(GmsUserDetails foundUser, Long id) {
		foundUser.setUserId(id);
		return foundUser;
	}
}
