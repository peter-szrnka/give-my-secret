package io.github.gms.functions.user;

import io.github.gms.common.types.Sensitive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -8637245107764207697L;
	@Sensitive
	private String oldCredential;
	@Sensitive
	private String newCredential;
}
