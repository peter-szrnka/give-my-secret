package io.github.gms.secure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.DATE_FORMAT;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

	private Long id;
	private Long userId;
	private boolean opened;
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime creationDate;
}
