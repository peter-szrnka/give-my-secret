package io.github.gms.common.enums;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import io.github.gms.functions.announcement.AnnouncementEntity;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.iprestriction.IpRestrictionEntity;
import io.github.gms.functions.keystore.KeystoreAliasEntity;
import io.github.gms.functions.keystore.KeystoreEntity;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.systemproperty.SystemPropertyEntity;
import io.github.gms.functions.user.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author Peter Szrnka
 * @since 1.0
*/
@Getter
@RequiredArgsConstructor
public enum EventTarget {

	ANNOUNCEMENT(AnnouncementEntity.class),
	IP_RESTRICTION(IpRestrictionEntity.class),
	API_KEY(ApiKeyEntity.class),
	SECRET(SecretEntity.class),
	KEYSTORE(KeystoreEntity.class),
	KEYSTORE_ALIAS(KeystoreAliasEntity.class),
	ADMIN_USER(null),
	USER(UserEntity.class),
	VALUES(null),
	SYSTEM_PROPERTY(SystemPropertyEntity.class),
	MAINTENANCE(null),
	UNKNOWN(null);

	private final Class<? extends AuditableGmsEntity> entityClassName;

	public static EventTarget getByClassName(String name) {
		return Arrays.stream(values())
				.filter(eventTarget -> eventTarget.getEntityClassName() != null && eventTarget.getEntityClassName().getSimpleName().equals(name))
				.findFirst()
				.orElse(UNKNOWN);
	}
}
