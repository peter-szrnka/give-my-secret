package io.github.gms.common.service;

import io.github.gms.functions.event.EventSource;

public class GmsThreadLocalValues {

    private static final ThreadLocal<EventSource> eventSourceThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    public static void setEventSource(EventSource eventSource) {
        eventSourceThreadLocal.set(eventSource);
    }

    public static EventSource getEventSource() {
        return eventSourceThreadLocal.get();
    }

    public static void removeEventSource() {
        eventSourceThreadLocal.remove();
    }

    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    public static void removeUserId() {
        userIdThreadLocal.remove();
    }
}
