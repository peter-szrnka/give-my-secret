package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileResponseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 6065853633292926550L;

	private String fileName;
	private byte[] fileContent;
}