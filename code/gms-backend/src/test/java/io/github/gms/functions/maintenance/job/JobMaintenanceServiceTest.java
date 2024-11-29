package io.github.gms.functions.maintenance.job;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.util.ConverterUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class JobMaintenanceServiceTest extends AbstractUnitTest {

    private JobRepository repository;
    private JobConverter converter;
    private JobMaintenanceService service;

    @BeforeEach
    public void setup() {
        repository = mock(JobRepository.class);
        converter = mock(JobConverter.class);
        service = new JobMaintenanceService(repository, converter);
    }

    @Test
    void list_whenValidInputProvided_thenReturnResultList() {
        // arrange
        Page<JobEntity> mockList = new PageImpl<>(List.of(new JobEntity()));
        when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
        when(converter.toDtoList(any())).thenReturn(JobListDto.builder().resultList(List.of(new JobDto())).build());
        Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

        // act
        JobListDto response = service.list(pageable);

        // assert
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(repository).findAll(any(Pageable.class));
        verify(converter).toDtoList(any());
    }
}
