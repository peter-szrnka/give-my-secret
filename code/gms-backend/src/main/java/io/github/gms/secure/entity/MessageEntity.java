package io.github.gms.secure.entity;

import java.time.ZonedDateTime;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Builder
@Table(name = "gms_message")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MessageEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 962278170039187346L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "message")
	private String message;
	
	@Column(name = "opened")
	@Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
	private boolean opened;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;
}
