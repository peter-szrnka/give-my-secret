package io.github.gms.auth.ldap.converter.impl;

import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.auth.ldap.converter.LdapUserConverter;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.abstraction.AbstractUserConverter;
import io.github.gms.functions.user.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_LDAP;
import static io.github.gms.common.util.Constants.LDAP_CRYPT_PREFIX;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Profile(value = { CONFIG_AUTH_TYPE_LDAP })
public class LdapUserConverterImpl extends AbstractUserConverter implements LdapUserConverter {

    private final Clock clock;
    private final SecretGenerator secretGenerator;
    @Setter
    @Value("${config.store.ldap.credential:false}")
    private boolean storeLdapCredential;

    @Override
    public UserEntity toEntity(GmsUserDetails foundUser, UserEntity existingEntity) {
        UserEntity entity = existingEntity == null ? new UserEntity() : existingEntity;

        entity.setStatus(foundUser.getStatus());
        entity.setName(foundUser.getName());
        entity.setUsername(foundUser.getUsername());
        entity.setCredential(getCredential(foundUser));
        entity.setCreationDate(ZonedDateTime.now(clock));
        entity.setEmail(foundUser.getEmail());
        entity.setRole(getFirstRole(foundUser.getAuthorities()));
        entity.setMfaEnabled(foundUser.isMfaEnabled());
        entity.setMfaSecret(secretGenerator.generate());
        return entity;
    }

    private String getCredential(GmsUserDetails foundUser) {
        return storeLdapCredential ? foundUser.getCredential().replace(LDAP_CRYPT_PREFIX, "")
                : "*PROVIDED_BY_LDAP*";
    }
}
