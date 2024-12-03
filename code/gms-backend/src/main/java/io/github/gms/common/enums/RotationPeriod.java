package io.github.gms.common.enums;

import lombok.Getter;

import java.time.temporal.ChronoUnit;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum RotationPeriod {

	MINUTES(ChronoUnit.MINUTES),
	HOURLY(ChronoUnit.HOURS),
	DAILY(ChronoUnit.DAYS),
	WEEKLY(ChronoUnit.WEEKS),
	MONTHLY(ChronoUnit.MONTHS),
	YEARLY(ChronoUnit.YEARS);
	
	private final ChronoUnit unit;
	private final Long unitValue;
	
	RotationPeriod(ChronoUnit unit) {
		this.unit = unit;
		this.unitValue = 1L;
	}
}