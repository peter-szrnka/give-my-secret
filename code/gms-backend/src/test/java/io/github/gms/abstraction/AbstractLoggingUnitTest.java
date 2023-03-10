package io.github.gms.abstraction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.common.enums.MdcParameter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractLoggingUnitTest extends AbstractUnitTest {

	protected ListAppender<ILoggingEvent> logAppender;

	@BeforeEach
	public void setup() {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		
		logAppender = new ListAppender<>();
		logAppender.start();
	}
	
	@AfterEach
	public void teardown() {
		MDC.remove(MdcParameter.USER_ID.getDisplayName());
		logAppender.list.clear();
		logAppender.stop();
	}
}
