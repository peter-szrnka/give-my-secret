package io.github.gms.common.entity;

import java.time.LocalDateTime;

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
import io.github.gms.common.enums.KeystoreType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_keystore")
@EqualsAndHashCode(callSuper = false)
public class KeystoreEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 7355366159631069375L;
	
	@Id
	@Column(name = "id")
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
	
	@Column(name = "alias", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String alias;
	
	@Column(name = "alias_credential", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String aliasCredential;
	
	@Column(name = "creation_date")
	private LocalDateTime creationDate;
}
