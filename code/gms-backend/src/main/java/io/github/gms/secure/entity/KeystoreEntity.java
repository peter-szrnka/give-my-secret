package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.db.converter.EncryptedFieldConverter;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeystoreType;
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

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_keystore")
@EqualsAndHashCode(callSuper = false)
public class KeystoreEntity extends AbstractGmsEntity {

	@Serial
	private static final long serialVersionUID = 7355366159631069375L;
	
	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EntityStatus status;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private KeystoreType type;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "credential", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String credential;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;
}
