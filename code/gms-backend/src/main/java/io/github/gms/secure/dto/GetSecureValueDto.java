package io.github.gms.secure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.KeyStoreValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSecureValueDto implements Serializable {

	private static final long serialVersionUID = -6290707659972986763L;
	private Long entityId;
	private Long aliasId;
	private KeyStoreValueType valueType;
}
