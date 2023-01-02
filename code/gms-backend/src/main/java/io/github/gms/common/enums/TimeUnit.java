package io.github.gms.common.enums;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public enum TimeUnit {

	HOUR("h", ChronoUnit.HOURS),
	DAY("d", ChronoUnit.DAYS),
	WEEK("w", ChronoUnit.WEEKS),
	MONTH("M", ChronoUnit.MONTHS),
	YEAR("y", ChronoUnit.YEARS);
	
	private TimeUnit(String code, ChronoUnit unit) {
		this.code = code;
		this.unit = unit;
	}

	private ChronoUnit unit;
	private String code;
	
	public static TimeUnit getByCode(String code) {
		return Arrays.stream(values()).filter(unit -> unit.code.equals(code)).findAny().orElse(TimeUnit.DAY);
	}

	public ChronoUnit getUnit() {
		return unit;
	}
}
