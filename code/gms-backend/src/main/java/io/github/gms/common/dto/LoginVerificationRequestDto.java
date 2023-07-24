package io.github.gms.common.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
public class LoginVerificationRequestDto implements Serializable {
    
    private Long userId;
    private String verificationCode;
}
