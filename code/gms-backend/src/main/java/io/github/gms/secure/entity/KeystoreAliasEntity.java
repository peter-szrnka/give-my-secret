package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.db.converter.EncryptedFieldConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_keystore_alias")
@EqualsAndHashCode(callSuper = false)
public class KeystoreAliasEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = -5558391835366268906L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "keystore_id")
	private Long keystoreId;
	
	@Column(name = "description", nullable = true)
	private String description;

	@Column(name = "alias", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String alias;

	@Column(name = "alias_credential", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String aliasCredential;
}
