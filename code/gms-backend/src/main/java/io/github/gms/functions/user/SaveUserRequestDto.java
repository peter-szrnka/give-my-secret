package io.github.gms.functions.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.types.Sensitive;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveUserRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -399277811201406211L;

	private Long id;
	@Sensitive
	private String name;
	@Sensitive
	private String username;
	@Sensitive
	private String email;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private EntityStatus status = EntityStatus.INITIAL;
	@Sensitive
	private String credential;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime creationDate;
	private UserRole role;
}
