package io.github.gms.common.util;

import org.slf4j.MDC;

import io.github.gms.common.enums.MdcParameter;

public class MdcUtils {
	
	private MdcUtils() {}

	public static Long getUserId() {
		return Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
	}
}