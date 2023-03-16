package io.github.gms.secure.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.util.Constants;
import lombok.Data;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
public class EventDto implements Serializable {

	private static final long serialVersionUID = 9068326085717719704L;

	private Long id;
	private Long userId;
	private String username;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
	private ZonedDateTime eventDate;
	private EventOperation operation;
	private EventTarget target;
}
