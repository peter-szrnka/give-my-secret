package io.github.gms.secure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 5446418144965465310L;

	private Long id;
	private String name;
	private String username;
	private String email;
	@Builder.Default
	private Set<UserRole> roles = new HashSet<>();
}
