package io.github.gms.common.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_event")
@EqualsAndHashCode(callSuper = false)
public class EventEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 4146919964684367885L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_name")
	private String userId;
	
	@Column(name = "event_date")
	private LocalDateTime eventDate;
	
	@Column(name = "operation")
	@Enumerated(EnumType.STRING)
	private EventOperation operation;
	
	@Column(name = "target")
	@Enumerated(EnumType.STRING)
	private EventTarget target;
}
