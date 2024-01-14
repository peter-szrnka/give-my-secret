package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "gms_user")
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends AbstractGmsEntity {

	@Serial
	private static final long serialVersionUID = 6223008984478998461L;
	
	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "user_name")
	private String username;

	@Column(name = "email")
	private String email;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EntityStatus status;

	@Column(name = "credential", nullable = true)
	private String credential;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;
	
	@Column(name = "roles")
	private String roles;

	@Column(name = "mfa_enabled")
	@Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
	private boolean mfaEnabled;

	@Column(name = "mfa_secret", nullable = true)
	private String mfaSecret;
}
