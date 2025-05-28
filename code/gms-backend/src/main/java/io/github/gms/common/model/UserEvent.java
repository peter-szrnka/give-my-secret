package io.github.gms.common.model;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.EventSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

	private Long userId;
	private Long entityId;
	private EventSource eventSource;
	private EventOperation operation;
	private EventTarget target;
	private ZonedDateTime eventDate;
}
