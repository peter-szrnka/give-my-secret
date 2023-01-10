package io.github.gms.secure.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.gms.common.enums.KeyStoreValueType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
