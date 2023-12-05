package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("SystemStatusDto")
public class SystemStatusDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 4843628264404173070L;

	private String authMode;
	private String status;
	private String version;
	private String built;
}
