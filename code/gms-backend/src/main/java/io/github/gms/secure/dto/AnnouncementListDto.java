package io.github.gms.secure.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class AnnouncementListDto implements Serializable {

	private static final long serialVersionUID = 3447191754511285579L;
	@Builder.Default
	private List<AnnouncementDto> resultList = new ArrayList<>();
	private long totalElements;
}
