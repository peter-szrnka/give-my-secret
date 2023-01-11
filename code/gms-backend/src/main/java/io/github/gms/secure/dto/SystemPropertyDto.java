package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPropertyDto implements Serializable {

	private static final long serialVersionUID = 9035787175353459355L;
	
	private String key;
	private String value;
	private LocalDateTime lastModified;
}