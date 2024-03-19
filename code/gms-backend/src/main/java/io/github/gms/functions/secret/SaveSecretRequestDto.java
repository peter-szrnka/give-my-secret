package io.github.gms.functions.secret;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SecretType;
import io.github.gms.functions.iprestriction.IpRestrictionDto;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveSecretRequestDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 8744862858176214735L;

	private Long id;
	private Long userId;
	private String secretId;
	private Long keystoreId;
	private Long keystoreAliasId;
	private String value;
	private EntityStatus status;
	private SecretType type;
	private RotationPeriod rotationPeriod;
	private boolean returnDecrypted;
	private boolean rotationEnabled;
	private Set<Long> apiKeyRestrictions = new HashSet<>();
	private List<IpRestrictionDto> ipRestrictions = new ArrayList<>();
}
