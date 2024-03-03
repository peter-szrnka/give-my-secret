package io.github.gms.common.enums;

import lombok.Getter;

import java.time.temporal.ChronoUnit;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum RotationPeriod {

	MINUTES(ChronoUnit.MINUTES, 1L),
	HOURLY(ChronoUnit.HOURS, 1L),
	DAILY(ChronoUnit.DAYS, 1L),
	WEEKLY(ChronoUnit.WEEKS, 1L),
	MONTHLY(ChronoUnit.MONTHS, 1L),
	YEARLY(ChronoUnit.YEARS, 1L);
	
	private final ChronoUnit unit;
	private final Long unitValue;
	
	RotationPeriod(ChronoUnit unit, Long unitValue) {
		this.unit = unit;
		this.unitValue = unitValue;
	}
}