package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooleanValueDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -7426749389683654760L;

    private Boolean value;
}
