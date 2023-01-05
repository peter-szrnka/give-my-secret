package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class SaveKeystoreRequestDto implements Serializable {

	private static final long serialVersionUID = -4849903722383712581L;
	private Long id;
	private Long userId;
	private String name;
	private EntityStatus status;
	private String description;
	private String credential;
	@JsonIgnore
	private LocalDateTime creationDate;
	private KeystoreType type;
	private List<KeystoreAliasDto> aliases;
}
