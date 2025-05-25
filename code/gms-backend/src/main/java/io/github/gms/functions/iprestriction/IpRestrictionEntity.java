package io.github.gms.functions.iprestriction;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import io.github.gms.common.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gms_ip_restriction")
@EqualsAndHashCode(callSuper = false)
public class IpRestrictionEntity extends AuditableGmsEntity {

    @Serial
    private static final long serialVersionUID = 7001126982750326766L;

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "secret_id")
    private Long secretId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @Column(name = "ip_pattern", nullable = false)
    private String ipPattern;

    @Column(name = "allow")
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    private boolean allow;

    @Column(name = "global")
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    private boolean global;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "last_modified")
    private ZonedDateTime lastModified;
}
