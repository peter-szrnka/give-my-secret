package io.github.gms.functions.maintenance.job;

import io.github.gms.common.enums.JobStatus;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {

    private Long id;
    private String name;
    private String correlationId;
    private ZonedDateTime creationDate;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Long duration;
    private JobStatus status;
    private String message;
}
