package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.SystemProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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