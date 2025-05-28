package io.github.gms.functions.user;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
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
@Table(name = "gms_user")
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends AuditableGmsEntity {

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

	@Column(name = "credential")
	private String credential;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;
	
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(name = "mfa_enabled")
	@Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
	private boolean mfaEnabled = false;

	@Column(name = "mfa_secret")
	private String mfaSecret;

	@Column(name = "failed_attempts")
	private Integer failedAttempts = 0;
}
