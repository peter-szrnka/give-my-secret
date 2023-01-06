package io.github.gms.common.event;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ToString
public class EntityChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = -722237695149809530L;
	
	@Getter
	private Map<String, Object> metadata;
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
		KEYSTORE_ALIAS_REMOVED("keystore alias");
		
		private EntityChangeType(String displayName) {
			this.displayName = displayName;
		}
		
		private String displayName;
		
		public String getDisplayName() {
			return displayName;
		}
	}
}