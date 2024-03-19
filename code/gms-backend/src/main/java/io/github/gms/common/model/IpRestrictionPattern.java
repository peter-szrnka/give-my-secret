package io.github.gms.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpRestrictionPattern implements Serializable {

    @Serial
    private static final long serialVersionUID = 1621438196059034453L;
    private String ipPattern;
    private boolean allow;
}
