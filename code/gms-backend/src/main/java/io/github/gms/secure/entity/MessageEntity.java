package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

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
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean opened;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;
}
