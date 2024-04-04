package io.github.gms.functions.user;

import io.github.gms.common.dto.UserInfoDto;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface UserInfoService {

	UserInfoDto getUserInfo(HttpServletRequest request);
}
