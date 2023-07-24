package io.github.gms.secure.service;

import java.io.UnsupportedEncodingException;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserListDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserService extends AbstractCrudService<SaveUserRequestDto, SaveEntityResponseDto, UserDto, UserListDto>, CountService {
	
	SaveEntityResponseDto saveAdminUser(SaveUserRequestDto dto);

	SaveEntityResponseDto save(SaveUserRequestDto dto);
	
	String getUsernameById(Long id);

	void changePassword(ChangePasswordRequestDto dto);

    String getMfaQrUrl() throws UnsupportedEncodingException;
}
