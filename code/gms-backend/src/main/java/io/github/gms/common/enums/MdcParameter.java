package io.github.gms.common.enums;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum MdcParameter {

	CORRELATION_ID("correlationId", false),
	USER_ID("userId"),
	USER_NAME("userName"),
	IS_ADMIN("isAdmin", false);
	
	private String displayName;
	private final boolean input;

	private MdcParameter(String displayName) {
		this.displayName = displayName;
		this.input = true;
	}
	
	private MdcParameter(String displayName, boolean input) {
		this.displayName = displayName;
		this.input = input;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public boolean isInput() {
		return input;
	}
}
