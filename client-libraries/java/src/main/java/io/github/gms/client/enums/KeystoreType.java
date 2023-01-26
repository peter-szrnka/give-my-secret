package io.github.gms.client.enums;

/**
 * @author Peter Szrnka
 * @since 1.0.0
 */
public enum KeystoreType {
	JKS("JKS"),
	PKCS12("PKCS12");
	
	private KeystoreType(String type) {
		this.type = type;
	}

	private String type;

	public String getType() {
		return type;
	}
}