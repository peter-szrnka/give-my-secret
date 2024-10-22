package io.github.gms.functions.maintenance.job;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class JobMaintenanceService {

    private final JobRepository repository;
    private final JobConverter converter;

    public JobListDto list(Pageable pageable) {
        return converter.toDtoList(repository.findAll(pageable));
    }
}
