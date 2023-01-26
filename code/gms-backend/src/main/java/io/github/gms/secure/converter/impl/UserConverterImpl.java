package io.github.gms.secure.converter.impl;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.entity.UserEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class UserConverterImpl implements UserConverter {
	
	@Autowired
	private Clock clock;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

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
		return new UserListDto(resultList.toList().stream().map(this::toDto).collect(Collectors.toList()));
	}

	@Override
	public UserInfoDto toUserInfoDto(GmsUserDetails user) {
		UserInfoDto dto = new UserInfoDto();
		dto.setId(user.getUserId());
		dto.setName(user.getName());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setRoles(Sets.newHashSet(user.getAuthorities().stream().map(authority -> UserRole.getByName(authority.getAuthority())).collect(Collectors.toSet())));

		return dto;
	}
}
