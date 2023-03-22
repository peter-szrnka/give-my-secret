package io.github.gms.common.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ToString
public class EntityChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = -722237695149809530L;
	
	@Getter
	private final Map<String, Object> metadata;
	private final EntityChangeType type;

	public EntityChangeEvent(Object source, Map<String, Object> metadata, EntityChangeType type) {
		super(source);
		this.metadata = metadata;
		this.type = type;
	}

	public EntityChangeType getType() {
		return type;
	}
	public enum EntityChangeType {
		KEYSTORE_DISABLED("keystore disabled"),
		KEYSTORE_DELETED("keystore deleted"),
		KEYSTORE_ALIAS_REMOVED("keystore alias");
		
		private EntityChangeType(String displayName) {
			this.displayName = displayName;
		}

		@Getter
		private final String displayName;
	}
}