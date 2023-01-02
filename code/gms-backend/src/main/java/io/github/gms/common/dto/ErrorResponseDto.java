package io.github.gms.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto implements Serializable {

	private static final long serialVersionUID = 6418018474049813605L;
	
	private String correlationId;
	private String message;
	private LocalDateTime timestamp;

	public ErrorResponseDto(String message, String correlationId, LocalDateTime timestamp) {
		this.message = message;
		this.correlationId = correlationId;
		this.timestamp = timestamp;
	}
}
