package io.github.gms.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.gms.common.enums.ContainerHostType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder(setterPrefix = "with")
@RedisHash("SystemStatusDto")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = SystemStatusDto.SystemStatusDtoBuilder.class)
public class SystemStatusDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 4843628264404173070L;

	private String authMode;
	private String status;
	private String version;
	private String built;
	private ContainerHostType containerHostType;
	private String containerId;
}
