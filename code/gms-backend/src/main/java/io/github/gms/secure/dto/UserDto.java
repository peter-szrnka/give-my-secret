package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

	private static final long serialVersionUID = -1043138827939793289L;

	private Long id;
	private String name;
	private String username;
	private String email;
	private EntityStatus status;
	private String credential;
	private ZonedDateTime creationDate;
	private Set<UserRole> roles = new HashSet<>();
}
