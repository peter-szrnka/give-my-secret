package io.github.gms.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class EntityDisabledEvent extends ApplicationEvent {

	private static final long serialVersionUID = -722237695149809530L;
	
	private final Long id;
	private final Long userId;
	private final EntityType type;

	public EntityDisabledEvent(Object source, Long userId, Long id, EntityType type) {
		super(source);
		this.userId = userId;
		this.id = id;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public EntityType getType() {
		return type;
	}

	public Long getUserId() {
		return userId;
	}

	public enum EntityType {
		API_KEY("API key"),
		KEYSTORE("keystore"),
		SECRET("secret");
		
		private EntityType(String displayName) {
			this.displayName = displayName;
		}
		
		private String displayName;
		
		public String getDisplayName() {
			return displayName;
		}
	}
}
