package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.SystemProperty;
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
@Table(name = "gms_system_property")
@EqualsAndHashCode(callSuper = false)
public class SystemPropertyEntity extends AbstractGmsEntity {

	@Serial
	private static final long serialVersionUID = 7838750742371446801L;
	
	@Id
	@Column(name = ID)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "prop_key")
	@Enumerated(EnumType.STRING)
	private SystemProperty key;
	
	@Column(name = "prop_value")
	private String value;

	@Column(name = "last_modified")
	private ZonedDateTime lastModified;
}