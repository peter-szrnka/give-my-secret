package io.github.gms.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ErrorResponseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 6418018474049813605L;
	
	private String correlationId;
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime timestamp;

	public ErrorResponseDto(String message, String correlationId, ZonedDateTime timestamp) {
		this.message = message;
		this.correlationId = correlationId;
		this.timestamp = timestamp;
	}
}
