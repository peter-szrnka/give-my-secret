package io.github.gms.functions.keystore;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.types.Sensitive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeystoreAliasDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -8584105411899700590L;

	private Long id;
	@Sensitive
	private String alias;
	@Sensitive
	private String aliasCredential;
	private AliasOperation operation;
	@Sensitive
	private String algorithm;
}