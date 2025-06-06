package io.github.gms.functions.secret;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_api_key_restriction")
@EqualsAndHashCode(callSuper = false)
public class ApiKeyRestrictionEntity extends AuditableGmsEntity {

	@Serial
	private static final long serialVersionUID = -6965366285964443078L;

	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "api_key_id")
	private Long apiKeyId;

	@Column(name = "secret_id")
	private Long secretId;
}
