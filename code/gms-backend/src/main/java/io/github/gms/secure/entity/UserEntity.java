package io.github.gms.secure.entity;

import java.time.ZonedDateTime;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.EntityStatus;
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
@Table(name = "gms_user")
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 6223008984478998461L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "user_name")
	private String username;

	@Column(name = "email")
	private String email;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EntityStatus status;

	@Column(name = "credential", nullable = true)
	private String credential;
	
	@Column(name = "creation_date")
	private ZonedDateTime creationDate;
	
	@Column(name = "roles")
	private String roles;
}
