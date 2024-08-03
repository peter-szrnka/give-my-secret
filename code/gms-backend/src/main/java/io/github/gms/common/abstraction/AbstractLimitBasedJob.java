package io.github.gms.common.abstraction;

import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.TimeUnit;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractLimitBasedJob extends AbstractJob {

	protected final Clock clock;

	protected AbstractLimitBasedJob(SystemService systemService, Clock clock, SystemPropertyService systemPropertyService) {
		super(systemService, systemPropertyService);
        this.clock = clock;
    }

	protected ZonedDateTime processConfig(SystemProperty limitProperty) {
		String oldEventLimitValue = systemPropertyService.get(limitProperty);
		String[] values = oldEventLimitValue.split(";");
		TimeUnit timeUnit = TimeUnit.getByCode(values[1]);
		return ZonedDateTime.now(clock).minus(Long.parseLong(values[0]), timeUnit.getUnit());
	}
}