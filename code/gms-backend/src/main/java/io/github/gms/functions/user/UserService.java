package io.github.gms.functions.user;

import dev.samstevens.totp.exceptions.QrGenerationException;
import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.service.CountService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserService extends AbstractCrudService<SaveUserRequestDto, SaveEntityResponseDto, UserDto, UserListDto>, CountService {
	
	SaveEntityResponseDto saveAdminUser(SaveUserRequestDto dto);

	SaveEntityResponseDto save(SaveUserRequestDto dto);
	
	String getUsernameById(Long id);

	void changePassword(ChangePasswordRequestDto dto);

    byte[] getMfaQrCode() throws QrGenerationException;

	void toggleMfa(boolean enabled);

    boolean isMfaActive();

	UserInfoDto getUserInfo(HttpServletRequest request);

	void updateLoginAttempt(String username);

	void resetLoginAttempt(String username);

	boolean isBlocked(String username);
}
