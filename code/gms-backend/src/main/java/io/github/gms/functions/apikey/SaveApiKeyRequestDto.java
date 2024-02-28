package io.github.gms.functions.apikey;

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
public class SaveApiKeyRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -8287465195543229193L;
	private Long id;
	private Long userId;
	private String name;
	private String value;
	private String description;
	private EntityStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime creationDate;
}
