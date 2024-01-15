package io.github.gms.common.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
public class LoginVerificationRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 260522957233658543L;
    private String username;
    private String verificationCode;
}
