package io.github.gms.client.model;

import java.io.InputStream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class GetSecretRequest {

	private String apiKey;
	private String secretId;
	private InputStream keystore;
	private String keystoreCredential;
	private String keystoreAlias;
	private String keystoreAliasCredential;

	private GetSecretRequest(Builder builder) {
		this.apiKey = builder.apiKey;
		this.secretId = builder.secretId;
		this.keystore = builder.keystore;
		this.keystoreCredential = builder.keystoreCredential;
		this.keystoreAlias = builder.keystoreAlias;
		this.keystoreAliasCredential = builder.keystoreAliasCredential;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private String apiKey;
		private String secretId;
		private InputStream keystore;
		private String keystoreCredential;
		private String keystoreAlias;
		private String keystoreAliasCredential;

		private Builder() {
		}

		public Builder withApiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder withSecretId(String secretId) {
			this.secretId = secretId;
			return this;
		}

		public Builder withKeystore(InputStream keystore) {
			this.keystore = keystore;
			return this;
		}

		public Builder withKeystoreCredential(String keystoreCredential) {
			this.keystoreCredential = keystoreCredential;
			return this;
		}

		public Builder withKeystoreAlias(String keystoreAlias) {
			this.keystoreAlias = keystoreAlias;
			return this;
		}

		public Builder withKeystoreAliasCredential(String keystoreAliasCredential) {
			this.keystoreAliasCredential = keystoreAliasCredential;
			return this;
		}

		public GetSecretRequest build() {
			return new GetSecretRequest(this);
		}
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public InputStream getKeystore() {
		return keystore;
	}

	public void setKeystore(InputStream keystore) {
		this.keystore = keystore;
	}

	public String getKeystoreCredential() {
		return keystoreCredential;
	}

	public void setKeystoreCredential(String keystoreCredential) {
		this.keystoreCredential = keystoreCredential;
	}

	public String getKeystoreAlias() {
		return keystoreAlias;
	}

	public void setKeystoreAlias(String keystoreAlias) {
		this.keystoreAlias = keystoreAlias;
	}

	public String getKeystoreAliasCredential() {
		return keystoreAliasCredential;
	}

	public void setKeystoreAliasCredential(String keystoreAliasCredential) {
		this.keystoreAliasCredential = keystoreAliasCredential;
	}

	@Override
	public String toString() {
		return "GetSecretRequest [apiKey=" + apiKey + ", secretId=" + secretId + ", keystore=" + keystore
				+ ", keystoreCredential=" + keystoreCredential + ", keystoreAlias=" + keystoreAlias
				+ ", keystoreAliasCredential=" + keystoreAliasCredential + "]";
	}
}