package io.github.gms.auth.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.gms.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GmsUserDetails implements UserDetails {

	private static final long serialVersionUID = 3437066490159576929L;
	private String name;
	private String email;
	private Long userId;
	private String username;
	private String credential;
	private Set<UserRole> authorities;
	@Builder.Default
	private Boolean accountNonLocked = true;
	@Builder.Default
	private Boolean enabled = true;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return credential;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
