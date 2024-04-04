package io.github.gms.auth.ldap.converter;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.functions.user.UserEntity;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface LdapUserConverter {

    UserEntity toEntity(GmsUserDetails foundUser, UserEntity existingEntity);
}
