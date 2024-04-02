package io.github.gms.functions.user;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.abstraction.GmsConverter;
import io.github.gms.common.dto.UserInfoDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserConverter extends GmsConverter<UserListDto, UserEntity> {

	UserEntity toNewEntity(SaveUserRequestDto dto, boolean roleChangeEnabled);

	UserEntity toEntity(UserEntity entity, SaveUserRequestDto dto, boolean roleChangeEnabled);
	
	UserDto toDto(UserEntity entity);

	UserInfoDto toUserInfoDto(GmsUserDetails user, boolean mfaRequired);

	GmsUserDetails addIdToUserDetails(GmsUserDetails first, Long id);
}
