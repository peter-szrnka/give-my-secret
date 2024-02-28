package io.github.gms.functions.user;

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
	private String oldCredential;
	private String newCredential;
}
