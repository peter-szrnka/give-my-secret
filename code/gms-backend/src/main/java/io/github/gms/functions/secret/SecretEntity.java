package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SecretType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_secret")
@EqualsAndHashCode(callSuper = false)
public class SecretEntity extends AuditableGmsEntity {

	@Serial
	private static final long serialVersionUID = 5114924217467033850L;
	
	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "keystore_alias_id")
	private Long keystoreAliasId;
	
	@Column(name = "secret_id")
	private String secretId;
	
	@Column(name = "value", length = 4000)
	private String value;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EntityStatus status;
	
	@Column(name = "secret_type")
	@Enumerated(EnumType.STRING)
	private SecretType type;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;

	@Column(name = "last_updated")
	private ZonedDateTime lastUpdated;

	@Column(name = "last_rotated")
	private ZonedDateTime lastRotated;
	
	@Column(name = "rotation_period")
	@Enumerated(EnumType.STRING)
	private RotationPeriod rotationPeriod;
	
	@Column(name = "return_decrypted")
	@Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
	private boolean returnDecrypted;
	
	@Column(name = "rotation_enabled")
	@Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
	private boolean rotationEnabled;
	
}
