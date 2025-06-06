package io.github.gms.functions.event;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.EventSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_event")
@EqualsAndHashCode(callSuper = false)
public class EventEntity extends AbstractGmsEntity {

	@Serial
	private static final long serialVersionUID = 4146919964684367885L;
	
	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "entity_id")
	private Long entityId;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "event_date")
	private ZonedDateTime eventDate;

	@Column(name = "source")
	@Enumerated(EnumType.STRING)
	private EventSource source;
	
	@Column(name = "operation")
	@Enumerated(EnumType.STRING)
	private EventOperation operation;
	
	@Column(name = "target")
	@Enumerated(EnumType.STRING)
	private EventTarget target;
}
