package io.github.gms.functions.announcement;

import io.github.gms.common.abstraction.AuditableGmsEntity;
import jakarta.persistence.*;
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
@Table(name = "gms_announcement")
@EqualsAndHashCode(callSuper = false)
public class AnnouncementEntity extends AuditableGmsEntity {

	@Serial
	private static final long serialVersionUID = 5171308736459567016L;

	@Id
	@Column(name = ID)
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
