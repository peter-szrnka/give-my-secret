package io.github.gms.common.enums;

import lombok.Getter;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Units: m=minute, d=day, M=month, y=year, w=week
 *
 * @author Peter Szrnka
 * @since 1.0
 */
public enum TimeUnit {

	HOUR("h", ChronoUnit.HOURS),
	DAY("d", ChronoUnit.DAYS),
	WEEK("w", ChronoUnit.WEEKS),
	MONTH("M", ChronoUnit.MONTHS),
	YEAR("y", ChronoUnit.YEARS);
	
	TimeUnit(String code, ChronoUnit unit) {
		this.code = code;
		this.unit = unit;
	}

	@Getter
	private final ChronoUnit unit;
	private final String code;
	
	public static TimeUnit getByCode(String code) {
		return Arrays.stream(values()).filter(unit -> unit.code.equals(code)).findAny().orElse(TimeUnit.DAY);
	}
}
