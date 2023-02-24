package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SecretType;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecretDto implements Serializable {

	private static final long serialVersionUID = -5511904398286163112L;

	private Long id;
	private Long userId;
	private Long keystoreId;
	private Long keystoreAliasId;
	private String secretId;
	private EntityStatus status;
	private SecretType type;
	private ZonedDateTime creationDate;
	private ZonedDateTime lastUpdated;
	private ZonedDateTime lastRotated;
	private RotationPeriod rotationPeriod;
	private boolean returnDecrypted;
	private boolean rotationEnabled;
	private Set<Long> apiKeyRestrictions;
}