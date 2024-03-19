package io.github.gms.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpRestrictionPatterns implements Serializable {

    @Serial
    private static final long serialVersionUID = -1724820252495121314L;

    private List<IpRestrictionPattern> items = new ArrayList<>();
}
