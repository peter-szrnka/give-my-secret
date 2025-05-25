package io.github.gms.common.util;

import io.github.gms.common.enums.MdcParameter;
import org.slf4j.MDC;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class MdcUtils {
	
	private MdcUtils() {}

	public static Long getUserId() {
		String mdcValue = MDC.get(MdcParameter.USER_ID.getDisplayName());
		return mdcValue == null ? null : Long.parseLong(mdcValue);
	}

	public static boolean isAdmin() {
		return Boolean.parseBoolean(MDC.get(MdcParameter.IS_ADMIN.getDisplayName()));
	}

	public static void put(MdcParameter mdcParameter, String value) {
		MDC.put(mdcParameter.getDisplayName(), value);
	}

	public static String get(MdcParameter mdcParameter) {
		return MDC.get(mdcParameter.getDisplayName());
	}

	public static void putLong(MdcParameter mdcParameter, Long value) {
		MDC.put(mdcParameter.getDisplayName(), String.valueOf(value));
	}

	public static Long getLong(MdcParameter mdcParameter) {
		return Long.parseLong(MDC.get(mdcParameter.getDisplayName()));
	}

	public static void remove(MdcParameter mdcParameter) {
		MDC.remove(mdcParameter.getDisplayName());
	}
}