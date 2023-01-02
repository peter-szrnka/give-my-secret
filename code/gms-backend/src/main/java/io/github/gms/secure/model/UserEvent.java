package io.github.gms.secure.model;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class UserEvent {

	private EventOperation operation;
	private EventTarget target;
}
