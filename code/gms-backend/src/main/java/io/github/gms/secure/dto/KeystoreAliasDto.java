package io.github.gms.secure.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.gms.common.enums.AliasOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeystoreAliasDto implements Serializable {

	private static final long serialVersionUID = -8584105411899700590L;

	private Long id;
	private String alias;
	private String aliasCredential;
	private AliasOperation operation;
}