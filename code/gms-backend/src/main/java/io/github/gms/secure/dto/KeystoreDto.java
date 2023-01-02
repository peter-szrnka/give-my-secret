package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeystoreType;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeystoreDto implements Serializable {

	private static final long serialVersionUID = -6962129766459594155L;

	private Long id;
	private Long userId;
	private EntityStatus status;
	private String name;
	private String fileName;
	private KeystoreType type;
	private String description;
	private String credential;
	private String alias;
	private String aliasCredential;
	private LocalDateTime creationDate;
}
