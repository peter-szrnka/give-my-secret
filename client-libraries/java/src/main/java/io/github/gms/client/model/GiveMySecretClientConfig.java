package io.github.gms.client.model;

/**
 * Client configuration
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
public class GiveMySecretClientConfig {

	private String url;
	private int defaultConnectionTimeout = 30000;
	private int defaultReadTimeout = 30000;
	
	public GiveMySecretClientConfig() {}

	private GiveMySecretClientConfig(Builder builder) {
		this.url = builder.url;
		this.defaultConnectionTimeout = builder.defaultConnectionTimeout;
		this.defaultReadTimeout = builder.defaultReadTimeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDefaultConnectionTimeout() {
		return defaultConnectionTimeout;
	}

	public void setDefaultConnectionTimeout(int defaultConnectionTimeout) {
		this.defaultConnectionTimeout = defaultConnectionTimeout;
	}

	public int getDefaultReadTimeout() {
		return defaultReadTimeout;
	}

	public void setDefaultReadTimeout(int defaultReadTimeout) {
		this.defaultReadTimeout = defaultReadTimeout;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private String url;
		private int defaultConnectionTimeout = 30000;
		private int defaultReadTimeout = 30000;

		private Builder() {
		}

		public Builder withUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder withDefaultConnectionTimeout(int defaultConnectionTimeout) {
			this.defaultConnectionTimeout = defaultConnectionTimeout;
			return this;
		}

		public Builder withDefaultReadTimeout(int defaultReadTimeout) {
			this.defaultReadTimeout = defaultReadTimeout;
			return this;
		}

		public GiveMySecretClientConfig build() {
			return new GiveMySecretClientConfig(this);
		}
	}
}