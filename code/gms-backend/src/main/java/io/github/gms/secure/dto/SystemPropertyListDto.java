package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPropertyListDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 8921222088599739123L;

	private List<SystemPropertyDto> resultList;
	private long totalElements;
}