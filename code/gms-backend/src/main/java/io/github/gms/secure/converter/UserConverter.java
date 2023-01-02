package io.github.gms.secure.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.common.entity.UserEntity;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.dto.UserListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserConverter extends GmsConverter<UserListDto, UserEntity> {

	UserEntity toNewEntity(SaveUserRequestDto dto, boolean roleChangeEnabled);

	UserEntity toEntity(UserEntity entity, SaveUserRequestDto dto, boolean roleChangeEnabled);
	
	UserDto toDto(UserEntity entity);

	UserInfoDto toUserInfoDto(GmsUserDetails user);
}
