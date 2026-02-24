package io.github.gms.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.experimental.UtilityClass;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Log assertion utility class.
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@UtilityClass
public class LogAssertionUtils {

    public void assertLogContains(ListAppender<ILoggingEvent> logAppender, String message) {
        assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().contains(message)));
    }

    public void assertLogEmpty(ListAppender<ILoggingEvent> logAppender) {
        assertTrue(logAppender.list.isEmpty());
    }

    public void assertLogEquals(ListAppender<ILoggingEvent> logAppender, String message) {
        assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().equals(message)));
    }

    public void assertLogEqualsIgnoreCase(ListAppender<ILoggingEvent> logAppender, String message) {
        assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().equalsIgnoreCase(message)));
    }

    public void assertLogMissing(ListAppender<ILoggingEvent> appender, String expectedMessage) {
        assertTrue(appender.list.stream().noneMatch(event -> event.getFormattedMessage().contains(expectedMessage)));
    }

    public void assertLogStartsWith(ListAppender<ILoggingEvent> logAppender, String message) {
        assertTrue(logAppender.list.stream().anyMatch(event -> event.getFormattedMessage().startsWith(message)));
    }
}
