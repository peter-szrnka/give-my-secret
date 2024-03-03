package io.github.gms.common.enums;

import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum KeystoreType {
	JKS("jks"),
	PKCS12("p12");

	KeystoreType(String extension) {
		this.fileExtension = extension;
	}

	private final String fileExtension;
}
