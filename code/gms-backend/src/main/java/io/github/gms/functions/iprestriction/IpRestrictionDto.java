package io.github.gms.functions.iprestriction;

import io.github.gms.common.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpRestrictionDto {

    private Long id;
    private Long userId;
    private Long secretId;
    private String ipPattern;
    private boolean allow;
    private EntityStatus status;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastModified;
}
