package io.github.gms.abstraction;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.common.UserIdExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractLoggingUnitTest extends AbstractUnitTest {

    @RegisterExtension
    private final UserIdExtension userIdExtension = new UserIdExtension();

	protected ListAppender<ILoggingEvent> logAppender;

	@BeforeEach
	public void setup() {
		logAppender = new ListAppender<>();
		logAppender.start();
	}
	
	@AfterEach
	public void tearDown() {
		logAppender.list.clear();
		logAppender.stop();
	}

	protected void addAppender(Class<?> clz) {
		((Logger) LoggerFactory.getLogger(clz)).addAppender(logAppender);
	}
}
