package io.github.gms.common.enums;

import io.github.gms.common.util.Constants;
import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum MdcParameter {

	CORRELATION_ID("correlationId", false),
	USER_ID(Constants.USER_ID),
	USER_NAME("userName"),
	IS_ADMIN("isAdmin", false),
	JOB_ID("jobId");
	
	private final String displayName;
	private final boolean input;

	MdcParameter(String displayName) {
		this.displayName = displayName;
		this.input = true;
	}
	
	MdcParameter(String displayName, boolean input) {
		this.displayName = displayName;
		this.input = input;
	}
}
