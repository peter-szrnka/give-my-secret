package io.github.gms.functions.keystore;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreFileService {

	long deleteTempKeystoreFiles();

	String generate(SaveKeystoreRequestDto dto);
}