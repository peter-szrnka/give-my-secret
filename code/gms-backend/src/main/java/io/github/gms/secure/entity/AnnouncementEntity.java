package io.github.gms.secure.entity;

import java.time.ZonedDateTime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "gms_announcement")
@EqualsAndHashCode(callSuper = false)
@ConditionalOnProperty(name = "config.x", havingValue = "true")
public class AnnouncementEntity extends AbstractGmsEntity {

	private static final long serialVersionUID = 5171308736459567016L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "author_id")
	private Long authorId;
	
	@Column(name = "announcement_date")
	private ZonedDateTime announcementDate;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description", length = 4000)
	private String description;
}
