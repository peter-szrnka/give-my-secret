package io.github.gms.common.service;

import io.github.gms.common.dto.ResetPasswordRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface ResetPasswordService {

    void resetPassword(ResetPasswordRequestDto dto);
}
