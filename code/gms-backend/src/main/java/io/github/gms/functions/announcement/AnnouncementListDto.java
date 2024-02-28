package io.github.gms.functions.announcement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
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

	@Serial
	private static final long serialVersionUID = 3447191754511285579L;
	@Builder.Default
	private List<AnnouncementDto> resultList = new ArrayList<>();
	private long totalElements;
}
