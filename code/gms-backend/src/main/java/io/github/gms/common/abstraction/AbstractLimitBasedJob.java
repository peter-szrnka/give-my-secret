package io.github.gms.common.abstraction;

import io.github.gms.common.enums.TimeUnit;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractLimitBasedJob {

	protected final Clock clock;

	protected AbstractLimitBasedJob(Clock clock) {
		this.clock = clock;
	}
	
	protected ZonedDateTime processConfig(String oldEventLimitValue) {
		String[] values = oldEventLimitValue.split(";");
		TimeUnit timeUnit = TimeUnit.getByCode(values[1]);
		return ZonedDateTime.now(clock).minus(Long.parseLong(values[0]), timeUnit.getUnit());
	}
}