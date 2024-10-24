package io.github.gms.functions.maintenance.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -8839092933626161502L;

    private List<JobDto> resultList;
    private long totalElements;
}
