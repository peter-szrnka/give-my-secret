package io.github.gms.functions.iprestriction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpRestrictionPattern {
    private String ipPattern;
    private boolean allow;
}
