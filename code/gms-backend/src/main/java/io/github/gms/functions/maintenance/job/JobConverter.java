package io.github.gms.functions.maintenance.job;

import io.github.gms.common.abstraction.GmsConverter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class JobConverter implements GmsConverter<JobListDto, JobEntity> {

    @Override
    public JobListDto toDtoList(Page<JobEntity> resultList) {
        List<JobDto> results = resultList.toList().stream().map(this::toDto).toList();
        return JobListDto.builder().resultList(results).totalElements(resultList.getTotalElements()).build();
    }

    private JobDto toDto(JobEntity entity) {
        return JobDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .creationDate(entity.getCreationDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .duration(entity.getDuration())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .build();
    }
}
