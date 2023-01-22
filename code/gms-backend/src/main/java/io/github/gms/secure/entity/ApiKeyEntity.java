package io.github.gms.secure.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.db.converter.EncryptedFieldConverter;
import io.github.gms.common.enums.EntityStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_api_key")
@EqualsAndHashCode(callSuper = false)
public class ApiKeyEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = -890551760657637824L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "name")
	private String name;

	@Column(name = "value", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String value = UUID.randomUUID().toString();
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EntityStatus status;
	
	@Column(name = "creation_date")
	private LocalDateTime creationDate;
}
