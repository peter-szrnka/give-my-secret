package io.github.gms.secure.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAnnouncementDto implements Serializable {

	private static final long serialVersionUID = -6459729562942743875L;
	private Long id;
	private String author;
	private String title;
	private String description;
}
