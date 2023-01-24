package io.github.gms.client.model;

public class GetSecretResponse {

	private String value;
	
	public GetSecretResponse() {}

	private GetSecretResponse(Builder builder) {
		this.value = builder.value;
	}

	public String getValue() {
		return value;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private String value;

		private Builder() {
		}

		public Builder withValue(String value) {
			this.value = value;
			return this;
		}

		public GetSecretResponse build() {
			return new GetSecretResponse(this);
		}
	}
}
