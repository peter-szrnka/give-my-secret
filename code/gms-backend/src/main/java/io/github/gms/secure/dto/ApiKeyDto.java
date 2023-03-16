package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.util.Constants;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
public class ApiKeyDto implements Serializable {

	private static final long serialVersionUID = 5818330544766866811L;
	private Long id;
	private Long userId;
	private String name;
	private String value;
	private String description;
	private EntityStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
	private ZonedDateTime creationDate;
}
