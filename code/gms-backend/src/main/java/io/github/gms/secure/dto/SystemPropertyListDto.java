package io.github.gms.secure.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class SystemPropertyListDto implements Serializable {

	private static final long serialVersionUID = 8921222088599739123L;

	private List<SystemPropertyDto> resultList;
}