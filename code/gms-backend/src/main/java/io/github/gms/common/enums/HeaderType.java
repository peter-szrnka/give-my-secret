package io.github.gms.common.enums;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum HeaderType {
	API_KEY("x-api-key", "apiKey"),
	AUTHORIZATION_TOKEN("AUTHORIZATION", "accessToken");
	
	private String headerName;
	private String mappedName;
	
	private HeaderType(String headerName, String mappedName) {
		this.headerName = headerName;
		this.mappedName = mappedName;
	}
	
	public String getHeaderName() {
		return headerName;
	}

	public String getMappedName() {
		return mappedName;
	}
}
