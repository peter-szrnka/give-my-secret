package io.github.gms.functions.event;

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
@NoArgsConstructor
@AllArgsConstructor
public class EventListDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -686851266183351444L;
	@Builder.Default
	private List<EventDto> resultList = new ArrayList<>();
	@Builder.Default
	private long totalElements = 0L;
}
