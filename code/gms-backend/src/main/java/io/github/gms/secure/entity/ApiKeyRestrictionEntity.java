package io.github.gms.secure.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_api_key_restriction")
@EqualsAndHashCode(callSuper = false)
public class ApiKeyRestrictionEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = -6965366285964443078L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "api_key_id")
	private Long apiKeyId;

	@Column(name = "secret_id", nullable = true)
	private Long secretId;
}
