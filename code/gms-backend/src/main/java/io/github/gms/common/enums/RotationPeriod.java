package io.github.gms.common.enums;

import java.time.temporal.ChronoUnit;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum RotationPeriod {

	MINUTES(ChronoUnit.MINUTES, 1l),
	HOURLY(ChronoUnit.HOURS, 1l),
	DAILY(ChronoUnit.DAYS, 1l),
	WEEKLY(ChronoUnit.WEEKS, 1l),
	MONTHLY(ChronoUnit.MONTHS, 1l),
	YEARLY(ChronoUnit.YEARS, 1l);
	
	private ChronoUnit unit;
	private Long unitValue;
	
	private RotationPeriod(ChronoUnit unit, Long unitValue) {
		this.unit = unit;
		this.unitValue = unitValue;
	}

	public ChronoUnit getUnit() {
		return unit;
	}

	public Long getUnitValue() {
		return unitValue;
	}
}
