package io.github.gms.common.enums;

import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public enum KeystoreType {
	JKS("jks"),
	PKCS12("p12");

	private KeystoreType(String extension) {
		this.fileExtension = extension;
	}

	@Getter
	private final String fileExtension;
}
