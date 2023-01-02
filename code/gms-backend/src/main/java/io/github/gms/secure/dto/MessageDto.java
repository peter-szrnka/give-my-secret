package io.github.gms.secure.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

	private Long id;
	private Long userId;
	private boolean opened;
	private String message;
	private LocalDateTime creationDate;
}
