package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementListDto implements Serializable {

	private static final long serialVersionUID = 3447191754511285579L;

	private List<AnnouncementDto> resultList;
	private long totalElements;
}
