package io.github.gms.common.util;

import io.github.gms.common.enums.MdcParameter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalContext {

    private static final ThreadLocal<Map<String, Object>> VARIABLES = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static void set(MdcParameter parameter, Object value) {
        VARIABLES.get().put(parameter.getDisplayName(), value);
    }

    public static Object get(MdcParameter parameter) {
        return VARIABLES.get().get(parameter.getDisplayName());
    }

    public static Boolean getAsBoolean(MdcParameter parameter) {
        return Boolean.parseBoolean(VARIABLES.get().get(parameter.getDisplayName()).toString());
    }

    public static String getAsString(MdcParameter parameter) {
        return (get(parameter) == null) ? null : String.valueOf(get(parameter));
    }

    public static Long getAsLong(MdcParameter parameter) {
        return Long.parseLong(VARIABLES.get().get(parameter.getDisplayName()).toString());
    }

    public static void remove(MdcParameter parameter) {
        VARIABLES.get().remove(parameter.getDisplayName());
    }
}
