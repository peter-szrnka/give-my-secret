package io.github.gms.secure.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SecretType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_secret")
@EqualsAndHashCode(callSuper = false)
public class SecretEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 5114924217467033850L;
	
	@Id
	@Column(name = "id")
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
	
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private SecretType type;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;

	@Column(name = "last_updated")
	private ZonedDateTime lastUpdated;

	@Column(name = "last_rotated")
	private ZonedDateTime lastRotated;
	
	@Column(name = "rotation_period", nullable = true)
	@Enumerated(EnumType.STRING)
	private RotationPeriod rotationPeriod;
	
	@Column(name = "return_decrypted")
	@Type(type = "org.hibernate.type.NumericBooleanType")  
	private boolean returnDecrypted;
	
	@Column(name = "rotation_enabled")
	@Type(type = "org.hibernate.type.NumericBooleanType")  
	private boolean rotationEnabled;
	
}
