package io.github.gms.common.enums;

import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum JwtConfigType {

	ACCESS_JWT(SystemProperty.ACCESS_JWT_ALGORITHM, SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS),
	REFRESH_JWT(SystemProperty.REFRESH_JWT_ALGORITHM, SystemProperty.REFRESH_JWT_EXPIRATION_TIME_SECONDS);
	
	JwtConfigType(SystemProperty algorithmProperty, SystemProperty expirationSecondsProperty) {
		this.algorithmProperty = algorithmProperty;
		this.expirationSecondsProperty = expirationSecondsProperty;
	}
	
	private final SystemProperty algorithmProperty;
	private final SystemProperty expirationSecondsProperty;
}