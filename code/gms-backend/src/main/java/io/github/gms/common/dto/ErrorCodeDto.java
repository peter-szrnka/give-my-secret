package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCodeDto {

    private String code;
    private String description;
}
