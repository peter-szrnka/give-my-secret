package io.github.gms.functions.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.DATE_FORMAT;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -1043138827939793289L;

	private Long id;
	private String name;
	private String username;
	private String email;
	private EntityStatus status;
	private String credential;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime creationDate;
	private UserRole role;
}
