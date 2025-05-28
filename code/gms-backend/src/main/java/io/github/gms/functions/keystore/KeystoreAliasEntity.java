package io.github.gms.functions.keystore;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import io.github.gms.common.db.converter.EncryptedFieldConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_keystore_alias")
@EqualsAndHashCode(callSuper = false)
public class KeystoreAliasEntity extends AuditableGmsEntity {

	@Serial
	private static final long serialVersionUID = -5558391835366268906L;

	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "keystore_id")
	private Long keystoreId;
	
	@Column(name = "description")
	private String description;

	@Column(name = "alias", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String alias;

	@Column(name = "alias_credential", length = 512)
	@Convert(converter = EncryptedFieldConverter.class)
	private String aliasCredential;

	@Column(name = "algorithm", length = 64)
	private String algorithm;
}
