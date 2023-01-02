package io.github.gms.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class RefreshCacheEvent extends ApplicationEvent {

	private static final long serialVersionUID = -4072151316990418427L;

	public RefreshCacheEvent(Object source) {
		super(source);
	}
}
