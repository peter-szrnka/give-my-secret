package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdListDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5146493197272681917L;
    private Set<Long> ids;
}
