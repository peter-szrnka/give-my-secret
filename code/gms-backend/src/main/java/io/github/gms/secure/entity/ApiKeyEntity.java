package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.db.converter.EncryptedFieldConverter;
import io.github.gms.common.enums.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import java.util.UUID;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_api_key")
@EqualsAndHashCode(callSuper = false)
public class ApiKeyEntity extends AbstractGmsEntity {

	@Serial
	private static final long serialVersionUID = -890551760657637824L;
	
	@Id
	@Column(name = ID)
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
	private ZonedDateTime creationDate;
}
