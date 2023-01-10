package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
public class SecretDto implements Serializable {

	private static final long serialVersionUID = -5511904398286163112L;

	private Long id;
	private Long userId;
	private Long keystoreId;
	private Long keystoreAliasId;
	private String secretId;
	private EntityStatus status;
	private LocalDateTime creationDate;
	private LocalDateTime lastUpdated;
	private LocalDateTime lastRotated;
	private RotationPeriod rotationPeriod;
	private boolean returnDecrypted;
	private boolean rotationEnabled;
	private Set<Long> apiKeyRestrictions;
}
