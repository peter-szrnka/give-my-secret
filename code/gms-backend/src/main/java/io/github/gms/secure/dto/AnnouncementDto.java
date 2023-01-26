package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

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
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDto implements Serializable {

	private static final long serialVersionUID = 4616633270410399698L;

	private Long id;
	private String author;
	private ZonedDateTime announcementDate;
	private String title;
	private String description;
}
