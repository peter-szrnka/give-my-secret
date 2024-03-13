package io.github.gms.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class HttpUtils {

    private static final List<String> IP_HEADERS = List.of(
            "X-Forwarded-For",
            "HTTP_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_VIA",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "REMOTE_ADDR"
    );

    private HttpUtils() {}

    public static String getClientIpAddress(HttpServletRequest request) {
        return IP_HEADERS.stream()
                .filter(header -> request.getHeader(header) != null && !request.getHeader(header).isEmpty())
                .findFirst().orElse(request.getRemoteAddr());
    }
}