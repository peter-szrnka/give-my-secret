package io.github.gms.common.abstraction;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.gms.common.enums.TimeUnit;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractLimitBasedJob {

	@Autowired
	protected Clock clock;
	
	protected ZonedDateTime processConfig(String oldEventLimitValue) {
		String[] values = oldEventLimitValue.split(";");
		TimeUnit timeUnit = TimeUnit.getByCode(values[1]);
		return ZonedDateTime.now(clock).minus(Long.parseLong(values[0]), timeUnit.getUnit());
	}
}