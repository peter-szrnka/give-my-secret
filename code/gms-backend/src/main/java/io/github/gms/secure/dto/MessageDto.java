package io.github.gms.secure.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private ZonedDateTime creationDate;
}
