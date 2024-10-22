package io.github.gms.functions.maintenance.job;

import io.github.gms.common.abstraction.AbstractGmsEntity;
import io.github.gms.common.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gms_job")
@EqualsAndHashCode(callSuper = false)
public class JobEntity extends AbstractGmsEntity {

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "start_time")
    private ZonedDateTime startTime;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "message")
    private String message;
}
