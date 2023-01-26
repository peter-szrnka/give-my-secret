package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import io.github.gms.common.enums.EntityStatus;
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
	private ZonedDateTime creationDate;
}
