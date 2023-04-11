package io.github.gms.secure.entity;

import java.time.ZonedDateTime;

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

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Table(name = "gms_system_property")
@EqualsAndHashCode(callSuper = false)
public class SystemPropertyEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 7838750742371446801L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "key")
	@Enumerated(EnumType.STRING)
	private SystemProperty key;
	
	@Column(name = "value")
	private String value;

	@Column(name = "last_modified")
	private ZonedDateTime lastModified;
}