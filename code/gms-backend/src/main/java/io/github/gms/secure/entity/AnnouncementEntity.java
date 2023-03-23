package io.github.gms.secure.entity;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
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
