package io.github.gms.secure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.gms.common.enums.EntityStatus;
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
public class ApiKeyDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 5818330544766866811L;
	private Long id;
	private Long userId;
	private String name;
	private String value;
	private String description;
	private EntityStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime creationDate;
}
