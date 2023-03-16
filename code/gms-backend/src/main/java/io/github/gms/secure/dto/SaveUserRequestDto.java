package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.util.Constants;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveUserRequestDto implements Serializable {

	private static final long serialVersionUID = -399277811201406211L;

	private Long id;
	private String name;
	private String username;
	private String email;
	private EntityStatus status;
	private String credential;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
	private ZonedDateTime creationDate;
	private Set<UserRole> roles = new HashSet<>();
}
