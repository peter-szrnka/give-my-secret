package io.github.gms.common.abstraction;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.TimeUnit;

import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractLimitBasedJob extends AbstractJob {

	protected ZonedDateTime processConfig(SystemProperty limitProperty) {
		String oldEventLimitValue = systemPropertyService.get(limitProperty);
		String[] values = oldEventLimitValue.split(";");
		TimeUnit timeUnit = TimeUnit.getByCode(values[1]);
		return ZonedDateTime.now(clock).minus(Long.parseLong(values[0]), timeUnit.getUnit());
	}
}