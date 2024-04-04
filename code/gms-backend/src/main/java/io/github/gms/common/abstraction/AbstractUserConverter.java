package io.github.gms.common.abstraction;

import io.github.gms.common.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public abstract class AbstractUserConverter {

    protected static UserRole getFirstRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(authority -> UserRole.getByName(authority.getAuthority()))
                .filter(Objects::nonNull)
                .toList()
                .getFirst();
    }
}
