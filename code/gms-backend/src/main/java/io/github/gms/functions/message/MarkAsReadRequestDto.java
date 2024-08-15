package io.github.gms.functions.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkAsReadRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 9078232383962461807L;
	private Set<Long> ids;
	private boolean opened;
}
