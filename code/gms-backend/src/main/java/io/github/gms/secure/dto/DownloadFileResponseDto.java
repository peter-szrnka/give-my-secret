package io.github.gms.secure.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileResponseDto implements Serializable {

	private static final long serialVersionUID = 6065853633292926550L;

	private String fileName;
	private byte[] fileContent;
}